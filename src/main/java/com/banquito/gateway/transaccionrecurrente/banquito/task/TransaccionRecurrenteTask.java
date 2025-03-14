package com.banquito.gateway.transaccionrecurrente.banquito.task;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.banquito.gateway.transaccionrecurrente.banquito.client.TransaccionSimpleClient;
import com.banquito.gateway.transaccionrecurrente.banquito.client.dto.TransaccionSimpleDTO;
import com.banquito.gateway.transaccionrecurrente.banquito.model.TransaccionRecurrente;
import com.banquito.gateway.transaccionrecurrente.banquito.service.TransaccionRecurrenteService;

@Component
public class TransaccionRecurrenteTask {

    private final Logger log = LoggerFactory.getLogger(TransaccionRecurrenteTask.class);
    private final TransaccionRecurrenteService service;
    private final TransaccionSimpleClient transaccionSimpleClient;
    
    private final Map<String, LocalDateTime> ultimaEjecucion = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> ultimoRechazo = new ConcurrentHashMap<>();
    private final Map<String, Integer> contadorReintentos = new ConcurrentHashMap<>();
    private static final int MAX_REINTENTOS = 3;
    private static final int MINUTOS_ESPERA_REINTENTO = 1;

    public TransaccionRecurrenteTask(TransaccionRecurrenteService service, TransaccionSimpleClient transaccionSimpleClient) {
        this.service = service;
        this.transaccionSimpleClient = transaccionSimpleClient;
    }

    @Scheduled(fixedRate = 10000)
    public void procesarTransaccionesRecurrentes() {
        log.info("Iniciando verificación de transacciones recurrentes: {}", LocalDateTime.now());
        
        List<TransaccionRecurrente> transaccionesActivas = service.obtenerPorEstado("ACT");
        
        log.info("Se encontraron {} transacciones recurrentes activas", transaccionesActivas.size());
        
        int transaccionesEnviadas = 0;

        for (TransaccionRecurrente transaccion : transaccionesActivas) {
            try {                
                if (debeEjecutarse(transaccion) || debeReintentarse(transaccion)) {
                    TransaccionSimpleDTO transaccionSimpleDTO = mapearATransaccionSimple(transaccion);

                    boolean esReintento = ultimoRechazo.containsKey(transaccion.getCodigo());
                    if (esReintento) {
                        log.info("Reintentando transacción {} (intento {})", 
                                transaccion.getCodigo(), 
                                contadorReintentos.get(transaccion.getCodigo()));
                    } else {
                        log.info("Enviando transacción {} al servicio externo", transaccion.getCodigo());
                    }

                    ResponseEntity<TransaccionSimpleDTO> respuesta = transaccionSimpleClient.ejecutarTransaccion(transaccionSimpleDTO);

                    if (respuesta.getStatusCode().is2xxSuccessful()) {
                        log.info("Transacción {} enviada exitosamente", transaccion.getCodigo());                                
                        service.actualizarDespuesDeEjecucion(transaccion.getCodigo());
                        transaccionesEnviadas++;
                        ultimaEjecucion.put(transaccion.getCodigo(), LocalDateTime.now());
                        // Limpiar datos de reintento si fue exitoso
                        ultimoRechazo.remove(transaccion.getCodigo());
                        contadorReintentos.remove(transaccion.getCodigo());
                    } else {
                        manejarTransaccionRechazada(transaccion);
                        log.error("Error al enviar transacción {}: Código de respuesta {}", 
                                 transaccion.getCodigo(), respuesta.getStatusCode().value());
                    }
                }
            } catch (Exception e) {
                manejarTransaccionRechazada(transaccion);
                log.error("Error al procesar transacción {}: {}", transaccion.getCodigo(), e.getMessage());
            }
        }
        
        if (transaccionesEnviadas > 0) {
            log.info("Finalizada verificación de transacciones recurrentes: {}. Se enviaron {} transacciones al microservicio externo", 
                     LocalDateTime.now(), transaccionesEnviadas);
        } else {
            log.info("Finalizada verificación de transacciones recurrentes: {}. No se enviaron transacciones al microservicio externo", 
                     LocalDateTime.now());
        }
    }

    private void manejarTransaccionRechazada(TransaccionRecurrente transaccion) {
        String codigo = transaccion.getCodigo();
        LocalDateTime ahora = LocalDateTime.now();
        
        // Inicializar o incrementar contador de reintentos
        int reintentos = contadorReintentos.getOrDefault(codigo, 0) + 1;
        contadorReintentos.put(codigo, reintentos);
        
        if (reintentos <= MAX_REINTENTOS) {
            ultimoRechazo.put(codigo, ahora);
            log.info("Programando reintento {} para la transacción {} en {} minuto(s)", 
                    reintentos, codigo, MINUTOS_ESPERA_REINTENTO);
        } else {
            log.error("La transacción {} ha excedido el número máximo de reintentos ({}). Cambiando estado a CANCELADO", 
                    codigo, MAX_REINTENTOS);
            try {
                service.actualizarEstado(codigo, "CAN");
                log.info("Estado de la transacción {} actualizado a CANCELADO", codigo);
            } catch (Exception e) {
                log.error("Error al actualizar estado de la transacción {} a CANCELADO: {}", 
                        codigo, e.getMessage());
            }
            ultimoRechazo.remove(codigo);
            contadorReintentos.remove(codigo);
        }
    }

    private boolean debeReintentarse(TransaccionRecurrente transaccion) {
        LocalDateTime ultimoRechazoTransaccion = ultimoRechazo.get(transaccion.getCodigo());
        if (ultimoRechazoTransaccion == null) {
            return false;
        }

        LocalDateTime ahora = LocalDateTime.now();
        long minutosDesdeRechazo = ChronoUnit.MINUTES.between(ultimoRechazoTransaccion, ahora);
        
        return minutosDesdeRechazo >= MINUTOS_ESPERA_REINTENTO;
    }
    
    private boolean debeEjecutarse(TransaccionRecurrente transaccion) {
        LocalDateTime fechaActual = LocalDateTime.now();
        LocalDateTime ultima = ultimaEjecucion.get(transaccion.getCodigo());
        
        Integer frecuenciaMinutos = transaccion.getFrecuenciaDias();
        if (frecuenciaMinutos == null || frecuenciaMinutos <= 0) {
            log.warn("Frecuencia no válida para la transacción {}, usando valor por defecto de 30 minutos", transaccion.getCodigo());
            frecuenciaMinutos = 30;
        }

        if (ultima == null) {
            log.info("Primera detección de la transacción {} con frecuencia {} minutos", 
                    transaccion.getCodigo(), frecuenciaMinutos);
            ultimaEjecucion.put(transaccion.getCodigo(), fechaActual);
            return false;
        }

        long minutosTranscurridos = ChronoUnit.MINUTES.between(ultima, fechaActual);
        LocalDateTime proximaEjecucion = ultima.plusMinutes(frecuenciaMinutos);

        boolean esHoraExacta = fechaActual.isAfter(proximaEjecucion) || fechaActual.isEqual(proximaEjecucion);
        boolean dentroDeVentana = fechaActual.getSecond() < 30;
        
        if (esHoraExacta && dentroDeVentana) {
            log.info("Ejecutando transacción {} - Frecuencia: {} minutos, Última ejecución: {}, Próxima programada: {}", 
                    transaccion.getCodigo(), frecuenciaMinutos, ultima, proximaEjecucion);
            return true;
        }
        
        return false;
    }
    
    private TransaccionSimpleDTO mapearATransaccionSimple(TransaccionRecurrente transaccion) {
        TransaccionSimpleDTO dto = new TransaccionSimpleDTO();
        
        dto.setCodTransaccion(UUID.randomUUID().toString().substring(0, 10));
        dto.setCodigoUnicoTransaccion(transaccion.getCodigo() + "-" + UUID.randomUUID().toString().substring(0, 5));
        dto.setTipo("PAG"); // Pago
        dto.setFecha(LocalDateTime.now());
        dto.setEstado("ACT");
        dto.setDiferido(false);
        dto.setMarca(transaccion.getMarca());
        dto.setMonto(transaccion.getMonto());
        dto.setMoneda(transaccion.getMoneda());
        dto.setPais(transaccion.getPais());
        
        if (transaccion.getTarjeta() != null) {
            dto.setNumeroTarjeta(transaccion.getTarjeta().toString());
        } else {
            log.warn("La tarjeta está vacía para la transacción {}, usando valor predeterminado", transaccion.getCodigo());
            dto.setNumeroTarjeta("4111111111111111");
        }
        
        if (transaccion.getFechaCaducidad() != null) {
            String mes = String.format("%02d", transaccion.getFechaCaducidad().getMonthValue());
            String anio = String.valueOf(transaccion.getFechaCaducidad().getYear() % 100);
            dto.setFechaExpiracion(mes + "/" + anio);
        } else {
            log.warn("La fecha de caducidad está vacía para la transacción {}, usando valor predeterminado", transaccion.getCodigo());
            dto.setFechaExpiracion("12/25");
        }
        
        dto.setSwift_banco(transaccion.getSwiftBanco());
        dto.setCuenta_iban(transaccion.getCuentaIban());
        
        if (transaccion.getCvv() != null && !transaccion.getCvv().isEmpty()) {
            try {
                dto.setCvv(Integer.parseInt(transaccion.getCvv()));
            } catch (NumberFormatException e) {
                log.warn("El CVV no es un número válido para la transacción {}, usando valor predeterminado", transaccion.getCodigo());
                dto.setCvv(123);
            }
        } else {
            log.warn("El CVV está vacío para la transacción {}, usando valor predeterminado", transaccion.getCodigo());
            dto.setCvv(123);
        }
        
        dto.setFrecuenciaDias(transaccion.getFrecuenciaDias());
        if (dto.getFrecuenciaDias() == null) {
            log.warn("La frecuencia está vacía para la transacción {}, usando valor predeterminado de 30 minutos", transaccion.getCodigo());
            dto.setFrecuenciaDias(30);
        }
        
        return dto;
    }
} 