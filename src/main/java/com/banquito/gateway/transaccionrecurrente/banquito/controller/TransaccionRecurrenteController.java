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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/transacciones-recurrentes")
@Tag(name = "Transacciones Recurrentes", description = "API para gestionar transacciones recurrentes de pagos automáticos como suscripciones, membresías y servicios periódicos")
public class TransaccionRecurrenteController {
    
    private final Logger log = LoggerFactory.getLogger(TransaccionRecurrenteController.class);
    private final TransaccionRecurrenteService service;
    private final TransaccionRecurrenteMapper mapper;

    public TransaccionRecurrenteController(TransaccionRecurrenteService service, TransaccionRecurrenteMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(
        summary = "Listar todas las transacciones recurrentes",
        description = "Obtiene un listado completo de todas las transacciones recurrentes registradas en el sistema, independientemente de su estado"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de transacciones recurrentes obtenida exitosamente", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = TransaccionRecurrenteDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<TransaccionRecurrenteDTO>> obtenerTodas() {
        log.info("Obteniendo todas las transacciones recurrentes");
        return ResponseEntity.ok(
            this.service.obtenerTodas().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/{codigo}")
    @Operation(
        summary = "Buscar transacción recurrente por código",
        description = "Obtiene una transacción recurrente específica utilizando su código único identificador"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transacción recurrente encontrada exitosamente", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = TransaccionRecurrenteDTO.class))),
        @ApiResponse(responseCode = "404", description = "Transacción recurrente no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<TransaccionRecurrenteDTO> obtenerPorCodigo(
            @Parameter(description = "Código único de la transacción recurrente", example = "ABC123XYZ", required = true) 
            @PathVariable String codigo) {
        log.info("Obteniendo transacción recurrente con código: {}", codigo);
        return ResponseEntity.ok(mapper.toDTO(this.service.obtenerPorCodigo(codigo)));
    }

    @GetMapping("/tarjeta/{tarjeta}")
    @Operation(
        summary = "Buscar transacciones por número de tarjeta",
        description = "Obtiene todas las transacciones recurrentes activas asociadas a una tarjeta específica"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de transacciones obtenida exitosamente", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = TransaccionRecurrenteDTO.class))),
        @ApiResponse(responseCode = "400", description = "Número de tarjeta inválido"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<TransaccionRecurrenteDTO>> obtenerPorTarjeta(
            @Parameter(description = "Número de tarjeta (16 dígitos)", example = "4532123456789012", required = true) 
            @PathVariable Long tarjeta) {
        log.info("Obteniendo transacciones recurrentes para la tarjeta: {}", tarjeta);
        return ResponseEntity.ok(
            this.service.obtenerPorTarjeta(tarjeta).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/cuenta/{cuentaIban}")
    @Operation(
        summary = "Buscar transacciones por cuenta IBAN",
        description = "Obtiene todas las transacciones recurrentes activas asociadas a una cuenta IBAN específica"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de transacciones obtenida exitosamente", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = TransaccionRecurrenteDTO.class))),
        @ApiResponse(responseCode = "400", description = "IBAN inválido"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<TransaccionRecurrenteDTO>> obtenerPorCuentaIban(
            @Parameter(description = "Número de cuenta en formato IBAN", example = "ES9121000418450200051332", required = true) 
            @PathVariable String cuentaIban) {
        log.info("Obteniendo transacciones recurrentes para la cuenta IBAN: {}", cuentaIban);
        return ResponseEntity.ok(
            this.service.obtenerPorCuentaIban(cuentaIban).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList())
        );
    }
    
    @GetMapping("/dia-mes/{diaMes}")
    @Operation(
        summary = "Buscar transacciones por día del mes",
        description = "Obtiene todas las transacciones recurrentes programadas para ejecutarse en un día específico del mes"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de transacciones obtenida exitosamente", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = TransaccionRecurrenteDTO.class))),
        @ApiResponse(responseCode = "400", description = "Día del mes inválido (debe estar entre 1 y 31)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<TransaccionRecurrenteDTO>> obtenerPorDiaMes(
            @Parameter(description = "Día del mes (1-31)", example = "15", required = true) 
            @PathVariable Integer diaMes) {
        log.info("Obteniendo transacciones recurrentes para el día del mes: {}", diaMes);
        return ResponseEntity.ok(
            this.service.obtenerPorDiaMes(diaMes).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList())
        );
    }
    
    @GetMapping("/estado/{estado}")
    @Operation(
        summary = "Buscar transacciones por estado",
        description = "Obtiene todas las transacciones recurrentes que se encuentran en un estado específico (ACT: Activas, INA: Inactivas, ELI: Eliminadas)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de transacciones obtenida exitosamente", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = TransaccionRecurrenteDTO.class))),
        @ApiResponse(responseCode = "400", description = "Estado inválido (debe ser ACT, INA o ELI)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<TransaccionRecurrenteDTO>> obtenerPorEstado(
            @Parameter(description = "Estado de la transacción (ACT, INA, ELI)", example = "ACT", required = true) 
            @PathVariable String estado) {
        log.info("Obteniendo transacciones recurrentes con estado: {}", estado);
        return ResponseEntity.ok(
            this.service.obtenerPorEstado(estado).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList())
        );
    }

    @PostMapping
    @Operation(
        summary = "Crear nueva transacción recurrente",
        description = "Registra una nueva transacción recurrente en el sistema para pagos automáticos periódicos"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Transacción recurrente creada exitosamente", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = TransaccionRecurrenteDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de transacción inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<TransaccionRecurrenteDTO> crear(
            @Parameter(description = "Datos de la nueva transacción recurrente", required = true) 
            @Valid @RequestBody TransaccionRecurrenteDTO transaccionDTO) {
        log.info("Creando nueva transacción recurrente");
        return ResponseEntity.status(201).body(
            mapper.toDTO(this.service.crear(mapper.toModel(transaccionDTO)))
        );
    }
} 