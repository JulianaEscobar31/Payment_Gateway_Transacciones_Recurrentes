package com.banquito.gateway.transaccionrecurrente.banquito.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        summary = "Listar transacciones recurrentes con paginación, ordenamiento y filtros",
        description = "Obtiene un listado de transacciones recurrentes con opciones de paginación, ordenamiento y filtrado"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de transacciones recurrentes obtenida exitosamente", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = TransaccionRecurrenteDTO.class))),
        @ApiResponse(responseCode = "400", description = "Parámetros de consulta inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Page<TransaccionRecurrenteDTO>> obtenerTodas(
            @Parameter(description = "Número de página (comenzando desde 0)", example = "0") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamaño de la página", example = "10") 
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Campo para ordenar (monto, diaMesPago, fechaInicio, etc)", example = "monto") 
            @RequestParam(required = false) String sort,
            
            @Parameter(description = "Dirección del ordenamiento (ASC o DESC)", example = "DESC") 
            @RequestParam(defaultValue = "ASC") String direction,
            
            @Parameter(description = "Filtrar por estado (ACT, INA, ELI)", example = "ACT") 
            @RequestParam(required = false) String estado,
            
            @Parameter(description = "Filtrar por día del mes de pago", example = "15") 
            @RequestParam(required = false) Integer diaMesPago,
            
            @Parameter(description = "Filtrar por país de origen", example = "EC") 
            @RequestParam(required = false) String pais,
            
            @Parameter(description = "Incluir transacciones eliminadas", example = "false") 
            @RequestParam(defaultValue = "false") boolean incluirEliminadas) {
        
        log.info("Obteniendo transacciones recurrentes paginadas: página={}, tamaño={}, ordenamiento={}, dirección={}", 
                 page, size, sort, direction);
        
        Pageable pageable;
        if (sort != null && !sort.isEmpty()) {
            Sort.Direction sortDirection = "DESC".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
            pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        } else {
            pageable = PageRequest.of(page, size);
        }
        
        Page<TransaccionRecurrenteDTO> transaccionesDTO = this.service.buscarTransacciones(estado, diaMesPago, pais, incluirEliminadas, pageable)
                .map(mapper::toDTO);
        
        return ResponseEntity.ok(transaccionesDTO);
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
        description = "Registra una nueva transacción recurrente en el sistema para pagos automáticos periódicos. El monto máximo permitido es de 100000 dólares."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Transacción recurrente creada exitosamente", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = TransaccionRecurrenteDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de transacción inválidos o monto superior al límite permitido"),
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