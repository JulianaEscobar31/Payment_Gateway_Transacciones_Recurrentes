package com.banquito.gateway.transaccionrecurrente.banquito.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.gateway.transaccionrecurrente.banquito.exception.EjecucionTransaccionException;
import com.banquito.gateway.transaccionrecurrente.banquito.exception.TransaccionRecurrenteInvalidaException;
import com.banquito.gateway.transaccionrecurrente.banquito.exception.TransaccionRecurrenteNotFoundException;
import com.banquito.gateway.transaccionrecurrente.banquito.service.EjecucionTransaccionRecurrenteService;
import com.banquito.gateway.transaccionrecurrente.banquito.service.TransaccionRecurrenteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/transaccionesrecurrentes/ejecucion")
@Tag(name = "Ejecución de Transacciones Recurrentes", description = "API para ejecutar transacciones recurrentes")
public class EjecucionTransaccionRecurrenteController {
    
    private final Logger log = LoggerFactory.getLogger(EjecucionTransaccionRecurrenteController.class);
    private final EjecucionTransaccionRecurrenteService ejecucionService;
    private final TransaccionRecurrenteService transaccionService;

    public EjecucionTransaccionRecurrenteController(
            EjecucionTransaccionRecurrenteService ejecucionService,
            TransaccionRecurrenteService transaccionService) {
        this.ejecucionService = ejecucionService;
        this.transaccionService = transaccionService;
    }

    @PostMapping("/programada")
    @Operation(summary = "Ejecutar todas las transacciones recurrentes programadas para hoy")
    @ApiResponse(responseCode = "200", description = "Ejecución de transacciones recurrentes iniciada")
    public ResponseEntity<String> ejecutarProgramadas() {
        log.info("Iniciando ejecución manual de todas las transacciones recurrentes programadas");
        
        try {
            ejecucionService.ejecutarTransaccionesRecurrentes();
            return ResponseEntity.ok("Ejecución de transacciones recurrentes iniciada");
        } catch (Exception e) {
            log.error("Error al ejecutar las transacciones recurrentes programadas: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
} 