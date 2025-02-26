package com.banquito.gateway.transaccionrecurrente.banquito.client.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.banquito.gateway.transaccionrecurrente.banquito.client.TransaccionSimpleClient;
import com.banquito.gateway.transaccionrecurrente.banquito.client.dto.TransaccionSimpleDTO;
import com.banquito.gateway.transaccionrecurrente.banquito.client.exception.EjecucionTransaccionException;
import com.banquito.gateway.transaccionrecurrente.banquito.model.TransaccionRecurrente;
import com.banquito.gateway.transaccionrecurrente.banquito.service.TransaccionRecurrenteService;

@Service
public class EjecucionTransaccionService {
    
    private final Logger log = LoggerFactory.getLogger(EjecucionTransaccionService.class);
    private final TransaccionRecurrenteService transaccionRecurrenteService;
    private final TransaccionSimpleClient transaccionSimpleClient;
    private final Map<String, LocalDateTime> ultimasEjecuciones;
    
    public EjecucionTransaccionService(TransaccionRecurrenteService transaccionRecurrenteService, 
                                      TransaccionSimpleClient transaccionSimpleClient) {
        this.transaccionRecurrenteService = transaccionRecurrenteService;
        this.transaccionSimpleClient = transaccionSimpleClient;
        this.ultimasEjecuciones = new HashMap<>();
    }
    
    @Scheduled(fixedRate = 300000) // Ejecutar cada 5 minutos (300000 ms)
    public void ejecutarTransaccionesRecurrentes() {
        log.info("Iniciando ejecución programada de transacciones recurrentes");
        
        // Obtener el día actual para buscar transacciones que deben ejecutarse hoy
        int diaActual = LocalDate.now().getDayOfMonth();
        List<TransaccionRecurrente> transaccionesParaEjecutar = 
                transaccionRecurrenteService.obtenerTransaccionesParaEjecutar(diaActual);
        
        log.info("Se encontraron {} transacciones para ejecutar", transaccionesParaEjecutar.size());
        
        for (TransaccionRecurrente transaccion : transaccionesParaEjecutar) {
            try {
                // Verificar si la transacción ya fue ejecutada recientemente
                if (!debeEjecutarTransaccion(transaccion.getCodigo())) {
                    log.info("Transacción {} ya fue ejecutada recientemente, se omitirá", transaccion.getCodigo());
                    continue;
                }
                
                // Ejecutar la transacción
                TransaccionSimpleDTO transaccionSimpleDTO = mapearATransaccionSimple(transaccion);
                log.info("Ejecutando transacción recurrente: {}", transaccion.getCodigo());
                
                try {
                    transaccionSimpleClient.ejecutarTransaccion(transaccionSimpleDTO);
                } catch (Exception e) {
                    throw new EjecucionTransaccionException(transaccion.getCodigo(), 
                            "Error al comunicarse con el servicio de transacciones simples", e);
                }
                
                // Registrar la ejecución exitosa
                ultimasEjecuciones.put(transaccion.getCodigo(), LocalDateTime.now());
                log.info("Transacción recurrente {} ejecutada exitosamente", transaccion.getCodigo());
                
            } catch (EjecucionTransaccionException e) {
                log.error(e.getMessage(), e);
            } catch (Exception e) {
                log.error("Error inesperado al ejecutar la transacción recurrente {}: {}", 
                        transaccion.getCodigo(), e.getMessage(), e);
            }
        }
    }
    
    public void ejecutarTransaccionManual(String codigoTransaccion) {
        log.info("Ejecutando manualmente la transacción recurrente: {}", codigoTransaccion);
        
        TransaccionRecurrente transaccion = transaccionRecurrenteService.obtenerPorCodigo(codigoTransaccion);
        
        if (transaccion.getEstado().equals("INA") || transaccion.getEstado().equals("ELI")) {
            throw new EjecucionTransaccionException(codigoTransaccion, 
                    "No se puede ejecutar una transacción inactiva o eliminada");
        }
        
        if (transaccion.getFechaFin() != null && transaccion.getFechaFin().isBefore(LocalDate.now())) {
            throw new EjecucionTransaccionException(codigoTransaccion, 
                    "No se puede ejecutar una transacción vencida");
        }
        
        TransaccionSimpleDTO transaccionSimpleDTO = mapearATransaccionSimple(transaccion);
        
        try {
            transaccionSimpleClient.ejecutarTransaccion(transaccionSimpleDTO);
            ultimasEjecuciones.put(transaccion.getCodigo(), LocalDateTime.now());
            log.info("Transacción recurrente {} ejecutada manualmente con éxito", codigoTransaccion);
        } catch (Exception e) {
            throw new EjecucionTransaccionException(codigoTransaccion, 
                    "Error al comunicarse con el servicio de transacciones simples", e);
        }
    }
    
    private boolean debeEjecutarTransaccion(String codigoTransaccion) {
        // Si no hay registro previo, debe ejecutarse
        if (!ultimasEjecuciones.containsKey(codigoTransaccion)) {
            return true;
        }
        
        // Verificar si han pasado al menos 24 horas desde la última ejecución
        LocalDateTime ultimaEjecucion = ultimasEjecuciones.get(codigoTransaccion);
        LocalDateTime ahora = LocalDateTime.now();
        
        // Si estamos en un día diferente al de la última ejecución, debe ejecutarse
        return ultimaEjecucion.toLocalDate().isBefore(ahora.toLocalDate());
    }
    
    private TransaccionSimpleDTO mapearATransaccionSimple(TransaccionRecurrente transaccion) {
        TransaccionSimpleDTO dto = new TransaccionSimpleDTO();
        
        dto.setCodigoTransaccion(UUID.randomUUID().toString().substring(0, 10));
        dto.setCodigoReferencia(transaccion.getCodigo());
        dto.setMonto(transaccion.getMonto());
        dto.setMoneda(transaccion.getMoneda());
        dto.setDescripcion("Pago recurrente: " + transaccion.getMarca());
        dto.setCuentaOrigen(transaccion.getCuentaIban());
        dto.setCuentaDestino(transaccion.getSwiftBanco());
        dto.setTipoTransaccion("RECURRENTE");
        dto.setEstado("PENDIENTE");
        dto.setFechaEjecucion(LocalDateTime.now());
        dto.setCanal("AUTOMATICO");
        dto.setMarca(transaccion.getMarca());
        dto.setPais(transaccion.getPais());
        dto.setTarjeta(transaccion.getTarjeta());
        
        return dto;
    }
} 