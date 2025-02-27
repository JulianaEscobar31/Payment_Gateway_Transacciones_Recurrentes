package com.banquito.gateway.transaccionrecurrente.banquito.controller;

import java.util.List;

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
import com.banquito.gateway.transaccionrecurrente.banquito.model.TransaccionRecurrente;
import com.banquito.gateway.transaccionrecurrente.banquito.service.EjecucionTransaccionRecurrenteService;
import com.banquito.gateway.transaccionrecurrente.banquito.service.TransaccionRecurrenteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/transaccionesrecurrentes/ejecucion")
@Tag(name = "Ejecución de Transacciones Recurrentes", description = "API para ejecutar transacciones recurrentes programadas o manualmente")
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
    @Operation(
        summary = "Ejecutar transacciones programadas para hoy",
        description = "Ejecuta todas las transacciones recurrentes activas programadas para el día actual del mes, enviándolas al microservicio de Transacción Simple"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ejecución de transacciones recurrentes iniciada", 
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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
    
    @PostMapping("/dia-mes/{diaMes}")
    @Operation(
        summary = "Ejecutar transacciones por día específico",
        description = "Ejecuta todas las transacciones recurrentes activas programadas para un día específico del mes, enviándolas al microservicio de Transacción Simple"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ejecución de transacciones recurrentes completada", 
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "400", description = "Día del mes inválido (debe estar entre 1 y 31)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<String> ejecutarPorDiaMes(
            @Parameter(description = "Día del mes (1-31)", example = "15", required = true) 
            @PathVariable Integer diaMes) {
        log.info("Iniciando ejecución de transacciones recurrentes para el día del mes: {}", diaMes);
        
        try {
            if (diaMes < 1 || diaMes > 31) {
                return ResponseEntity.badRequest().body("El día del mes debe estar entre 1 y 31");
            }
            
            List<TransaccionRecurrente> transacciones = transaccionService.obtenerPorDiaMes(diaMes);
            log.info("Se encontraron {} transacciones para el día {}", transacciones.size(), diaMes);
            
            int ejecutadas = 0;
            for (TransaccionRecurrente transaccion : transacciones) {
                try {
                    if ("ACT".equals(transaccion.getEstado())) {
                        ejecucionService.ejecutarTransaccion(transaccion);
                        ejecutadas++;
                    }
                } catch (Exception e) {
                    log.error("Error al ejecutar la transacción {}: {}", transaccion.getCodigo(), e.getMessage());
                }
            }
            
            return ResponseEntity.ok(String.format("Se ejecutaron %d de %d transacciones para el día %d", 
                    ejecutadas, transacciones.size(), diaMes));
        } catch (Exception e) {
            log.error("Error al ejecutar las transacciones para el día {}: {}", diaMes, e.getMessage());
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
} 