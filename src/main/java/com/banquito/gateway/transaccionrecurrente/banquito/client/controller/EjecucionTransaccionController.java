package com.banquito.gateway.transaccionrecurrente.banquito.client.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.gateway.transaccionrecurrente.banquito.client.exception.EjecucionTransaccionException;
import com.banquito.gateway.transaccionrecurrente.banquito.client.service.EjecucionTransaccionService;
import com.banquito.gateway.transaccionrecurrente.banquito.exception.TransaccionRecurrenteNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/transaccionesrecurrentes/ejecucion")
@Tag(name = "Ejecución de Transacciones Recurrentes", description = "API para ejecutar transacciones recurrentes")
public class EjecucionTransaccionController {
    
    private final Logger log = LoggerFactory.getLogger(EjecucionTransaccionController.class);
    private final EjecucionTransaccionService service;
    
    public EjecucionTransaccionController(EjecucionTransaccionService service) {
        this.service = service;
    }
    
    @PostMapping("/ejecutar")
    @Operation(summary = "Ejecutar manualmente las transacciones recurrentes del día actual")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transacciones recurrentes ejecutadas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error al ejecutar las transacciones recurrentes")
    })
    public ResponseEntity<String> ejecutarTransaccionesRecurrentes() {
        log.info("Solicitud manual para ejecutar transacciones recurrentes");
        try {
            service.ejecutarTransaccionesRecurrentes();
            return ResponseEntity.ok("Transacciones recurrentes ejecutadas exitosamente");
        } catch (Exception e) {
            log.error("Error al ejecutar transacciones recurrentes: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error al ejecutar transacciones recurrentes: " + e.getMessage());
        }
    }
    
    @PostMapping("/ejecutar/{codigo}")
    @Operation(summary = "Ejecutar manualmente una transacción recurrente específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transacción recurrente ejecutada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Transacción recurrente no encontrada"),
        @ApiResponse(responseCode = "400", description = "Error al ejecutar la transacción recurrente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<String> ejecutarTransaccionRecurrente(
            @Parameter(description = "Código de la transacción recurrente") 
            @PathVariable String codigo) {
        log.info("Solicitud manual para ejecutar la transacción recurrente: {}", codigo);
        try {
            service.ejecutarTransaccionManual(codigo);
            return ResponseEntity.ok("Transacción recurrente ejecutada exitosamente");
        } catch (TransaccionRecurrenteNotFoundException e) {
            log.error("Transacción recurrente no encontrada: {}", codigo);
            return ResponseEntity.notFound().build();
        } catch (EjecucionTransaccionException e) {
            log.error("Error al ejecutar la transacción recurrente: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error interno al ejecutar la transacción recurrente: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error interno al ejecutar la transacción recurrente: " + e.getMessage());
        }
    }
} 