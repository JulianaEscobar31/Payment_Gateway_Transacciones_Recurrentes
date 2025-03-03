package com.banquito.gateway.transaccionrecurrente.banquito.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    public TransaccionRecurrenteTask(TransaccionRecurrenteService service, TransaccionSimpleClient transaccionSimpleClient) {
        this.service = service;
        this.transaccionSimpleClient = transaccionSimpleClient;
    }

    @Scheduled(fixedRate = 120000)
    public void procesarTransaccionesRecurrentes() {
        log.info("Iniciando procesamiento de transacciones recurrentes: {}", LocalDateTime.now());

        int diaActual = LocalDate.now().getDayOfMonth();
        
        List<TransaccionRecurrente> transaccionesParaEjecutar = service.obtenerTransaccionesParaEjecutar(diaActual);
        
        log.info("Se encontraron {} transacciones recurrentes para ejecutar", transaccionesParaEjecutar.size());

        for (TransaccionRecurrente transaccion : transaccionesParaEjecutar) {
            try {
                TransaccionSimpleDTO transaccionSimpleDTO = mapearATransaccionSimple(transaccion);

                log.info("Enviando transacción {} al servicio externo", transaccion.getCodigo());
                ResponseEntity<TransaccionSimpleDTO> respuesta = transaccionSimpleClient.ejecutarTransaccion(transaccionSimpleDTO);

                if (respuesta.getStatusCode().is2xxSuccessful()) {
                    log.info("Transacción {} enviada exitosamente", transaccion.getCodigo());                
                    service.actualizarDespuesDeEjecucion(transaccion.getCodigo());
                } else {
                    log.error("Error al enviar transacción {}: Código de respuesta {}", 
                             transaccion.getCodigo(), respuesta.getStatusCode().value());
                }
            } catch (Exception e) {
                log.error("Error al procesar transacción {}: {}", transaccion.getCodigo(), e.getMessage());
            }
        }
        
        log.info("Finalizado procesamiento de transacciones recurrentes: {}", LocalDateTime.now());
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
            log.warn("La frecuencia de días está vacía para la transacción {}, usando valor predeterminado", transaccion.getCodigo());
            dto.setFrecuenciaDias(30);
        }
        
        return dto;
    }
} 