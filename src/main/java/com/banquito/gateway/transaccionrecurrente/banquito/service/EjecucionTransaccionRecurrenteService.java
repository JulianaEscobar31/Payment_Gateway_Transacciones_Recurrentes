package com.banquito.gateway.transaccionrecurrente.banquito.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banquito.gateway.transaccionrecurrente.banquito.client.TransaccionSimpleClient;
import com.banquito.gateway.transaccionrecurrente.banquito.client.dto.TransaccionSimpleDTO;
import com.banquito.gateway.transaccionrecurrente.banquito.exception.TransaccionRecurrenteInvalidaException;
import com.banquito.gateway.transaccionrecurrente.banquito.model.TransaccionRecurrente;
import com.banquito.gateway.transaccionrecurrente.banquito.repository.TransaccionRecurrenteRepository;

@Service
public class EjecucionTransaccionRecurrenteService {
    
    private final Logger log = LoggerFactory.getLogger(EjecucionTransaccionRecurrenteService.class);
    private final TransaccionRecurrenteRepository repository;
    private final TransaccionSimpleClient transaccionSimpleClient;

    public EjecucionTransaccionRecurrenteService(TransaccionRecurrenteRepository repository, 
                                                TransaccionSimpleClient transaccionSimpleClient) {
        this.repository = repository;
        this.transaccionSimpleClient = transaccionSimpleClient;
    }

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void ejecutarTransaccionesRecurrentes() {
        log.info("Iniciando ejecución programada de transacciones recurrentes");

        int diaActual = LocalDate.now().getDayOfMonth();
        List<TransaccionRecurrente> transaccionesParaEjecutar = repository.findByEstadoAndDiaMesPagoAndFechaFinGreaterThanEqual(
                "ACT", diaActual, LocalDate.now());
        
        log.info("Se encontraron {} transacciones recurrentes para ejecutar", transaccionesParaEjecutar.size());
        
        for (TransaccionRecurrente transaccion : transaccionesParaEjecutar) {
            try {
                ejecutarTransaccion(transaccion);
            } catch (Exception e) {
                log.error("Error al ejecutar la transacción recurrente {}: {}", transaccion.getCodigo(), e.getMessage());
            }
        }
        
        log.info("Finalizada la ejecución programada de transacciones recurrentes");
    }
    
    @Transactional
    public void ejecutarTransaccion(TransaccionRecurrente transaccion) {
        log.info("Ejecutando transacción recurrente: {}", transaccion.getCodigo());

        if (!"ACT".equals(transaccion.getEstado())) {
            throw new TransaccionRecurrenteInvalidaException("La transacción no está activa");
        }

        if (transaccion.getFechaCaducidad() != null && transaccion.getFechaCaducidad().isBefore(LocalDate.now())) {
            throw new TransaccionRecurrenteInvalidaException("La tarjeta ha caducado");
        }

        TransaccionSimpleDTO transaccionSimpleDTO = new TransaccionSimpleDTO();
        transaccionSimpleDTO.setCodigoTransaccion(UUID.randomUUID().toString().substring(0, 10));
        transaccionSimpleDTO.setCodigoTransaccionRecurrente(transaccion.getCodigo());
        transaccionSimpleDTO.setMonto(transaccion.getMonto());
        transaccionSimpleDTO.setMarca(transaccion.getMarca());
        transaccionSimpleDTO.setSwiftBanco(transaccion.getSwiftBanco());
        transaccionSimpleDTO.setCuentaIban(transaccion.getCuentaIban());
        transaccionSimpleDTO.setMoneda(transaccion.getMoneda());
        transaccionSimpleDTO.setPais(transaccion.getPais());
        transaccionSimpleDTO.setTarjeta(transaccion.getTarjeta());
        transaccionSimpleDTO.setFechaEjecucion(LocalDateTime.now());
        transaccionSimpleDTO.setEstado("PEN");
        
        try {
            log.info("Enviando transacción al microservicio de transacción simple: {}", transaccionSimpleDTO);
            transaccionSimpleClient.ejecutarTransaccion(transaccionSimpleDTO);
            log.info("Transacción enviada exitosamente");
        } catch (Exception e) {
            log.error("Error al enviar la transacción al microservicio: {}", e.getMessage());
            throw new TransaccionRecurrenteInvalidaException("Error al procesar la transacción: " + e.getMessage());
        }
    }
} 