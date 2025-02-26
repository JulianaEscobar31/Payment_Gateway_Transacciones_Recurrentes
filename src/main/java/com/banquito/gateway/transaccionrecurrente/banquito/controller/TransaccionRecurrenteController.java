package com.banquito.gateway.transaccionrecurrente.banquito.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.gateway.transaccionrecurrente.banquito.controller.dto.TransaccionRecurrenteDTO;
import com.banquito.gateway.transaccionrecurrente.banquito.controller.mapper.TransaccionRecurrenteMapper;
import com.banquito.gateway.transaccionrecurrente.banquito.service.TransaccionRecurrenteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/transaccionesrecurrentes")
@Tag(name = "Transacciones Recurrentes", description = "API para gestionar transacciones recurrentes")
public class TransaccionRecurrenteController {
    
    private final Logger log = LoggerFactory.getLogger(TransaccionRecurrenteController.class);
    private final TransaccionRecurrenteService service;
    private final TransaccionRecurrenteMapper mapper;

    public TransaccionRecurrenteController(TransaccionRecurrenteService service, TransaccionRecurrenteMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las transacciones recurrentes")
    @ApiResponse(responseCode = "200", description = "Lista de transacciones recurrentes obtenida exitosamente")
    public ResponseEntity<List<TransaccionRecurrenteDTO>> obtenerTodas() {
        log.info("Obteniendo todas las transacciones recurrentes");
        return ResponseEntity.ok(
            this.service.obtenerTodas().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener una transacción recurrente por su código")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transacción recurrente encontrada"),
        @ApiResponse(responseCode = "404", description = "Transacción recurrente no encontrada")
    })
    public ResponseEntity<TransaccionRecurrenteDTO> obtenerPorCodigo(
            @Parameter(description = "Código de la transacción recurrente") 
            @PathVariable String codigo) {
        log.info("Obteniendo transacción recurrente con código: {}", codigo);
        return ResponseEntity.ok(mapper.toDTO(this.service.obtenerPorCodigo(codigo)));
    }

    @GetMapping("/tarjeta/{tarjeta}")
    @Operation(summary = "Obtener transacciones recurrentes por número de tarjeta")
    @ApiResponse(responseCode = "200", description = "Lista de transacciones recurrentes obtenida exitosamente")
    public ResponseEntity<List<TransaccionRecurrenteDTO>> obtenerPorTarjeta(
            @Parameter(description = "Número de tarjeta") 
            @PathVariable Long tarjeta) {
        log.info("Obteniendo transacciones recurrentes para la tarjeta: {}", tarjeta);
        return ResponseEntity.ok(
            this.service.obtenerPorTarjeta(tarjeta).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/cuenta/{cuentaIban}")
    @Operation(summary = "Obtener transacciones recurrentes por cuenta IBAN")
    @ApiResponse(responseCode = "200", description = "Lista de transacciones recurrentes obtenida exitosamente")
    public ResponseEntity<List<TransaccionRecurrenteDTO>> obtenerPorCuentaIban(
            @Parameter(description = "Número de cuenta IBAN") 
            @PathVariable String cuentaIban) {
        log.info("Obteniendo transacciones recurrentes para la cuenta IBAN: {}", cuentaIban);
        return ResponseEntity.ok(
            this.service.obtenerPorCuentaIban(cuentaIban).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList())
        );
    }

    @PostMapping
    @Operation(summary = "Crear una nueva transacción recurrente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transacción recurrente creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de transacción recurrente inválidos")
    })
    public ResponseEntity<TransaccionRecurrenteDTO> crear(
            @Parameter(description = "Datos de la transacción recurrente") 
            @Valid @RequestBody TransaccionRecurrenteDTO transaccionDTO) {
        log.info("Creando nueva transacción recurrente");
        return ResponseEntity.ok(
            mapper.toDTO(this.service.crear(mapper.toModel(transaccionDTO)))
        );
    }

    @PutMapping("/{codigo}")
    @Operation(summary = "Actualizar una transacción recurrente existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transacción recurrente actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de transacción recurrente inválidos"),
        @ApiResponse(responseCode = "404", description = "Transacción recurrente no encontrada")
    })
    public ResponseEntity<TransaccionRecurrenteDTO> actualizar(
            @Parameter(description = "Código de la transacción recurrente") 
            @PathVariable String codigo,
            @Parameter(description = "Datos actualizados de la transacción recurrente") 
            @Valid @RequestBody TransaccionRecurrenteDTO transaccionDTO) {
        log.info("Actualizando transacción recurrente con código: {}", codigo);
        return ResponseEntity.ok(
            mapper.toDTO(this.service.actualizar(codigo, mapper.toModel(transaccionDTO)))
        );
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Eliminar una transacción recurrente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transacción recurrente eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Transacción recurrente no encontrada")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Código de la transacción recurrente") 
            @PathVariable String codigo) {
        log.info("Eliminando transacción recurrente con código: {}", codigo);
        this.service.eliminar(codigo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ejecutar/{diaPago}")
    @Operation(summary = "Obtener transacciones recurrentes para ejecutar en un día específico")
    @ApiResponse(responseCode = "200", description = "Lista de transacciones recurrentes obtenida exitosamente")
    public ResponseEntity<List<TransaccionRecurrenteDTO>> obtenerTransaccionesParaEjecutar(
            @Parameter(description = "Día del mes para ejecutar las transacciones") 
            @PathVariable Integer diaPago) {
        log.info("Obteniendo transacciones recurrentes para ejecutar en el día: {}", diaPago);
        return ResponseEntity.ok(
            this.service.obtenerTransaccionesParaEjecutar(diaPago).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/vencidas")
    @Operation(summary = "Obtener transacciones recurrentes vencidas")
    @ApiResponse(responseCode = "200", description = "Lista de transacciones recurrentes vencidas obtenida exitosamente")
    public ResponseEntity<List<TransaccionRecurrenteDTO>> obtenerTransaccionesVencidas() {
        log.info("Obteniendo transacciones recurrentes vencidas");
        return ResponseEntity.ok(
            this.service.obtenerTransaccionesVencidas().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList())
        );
    }
} 