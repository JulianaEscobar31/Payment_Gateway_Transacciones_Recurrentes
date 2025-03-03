package com.banquito.gateway.transaccionrecurrente.banquito.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banquito.gateway.transaccionrecurrente.banquito.exception.TransaccionRecurrenteInvalidaException;
import com.banquito.gateway.transaccionrecurrente.banquito.exception.TransaccionRecurrenteNotFoundException;
import com.banquito.gateway.transaccionrecurrente.banquito.model.TransaccionRecurrente;
import com.banquito.gateway.transaccionrecurrente.banquito.repository.TransaccionRecurrenteRepository;

@Service
public class TransaccionRecurrenteService {
    
    private final Logger log = LoggerFactory.getLogger(TransaccionRecurrenteService.class);
    private final TransaccionRecurrenteRepository repository;

    public TransaccionRecurrenteService(TransaccionRecurrenteRepository repository) {
        this.repository = repository;
    }

    public List<TransaccionRecurrente> obtenerTodas() {
        log.info("Obteniendo todas las transacciones recurrentes");
        return this.repository.findAll();
    }

    public Page<TransaccionRecurrente> buscarTransacciones(String estado, Integer diaMesPago, String pais, boolean incluirEliminadas, Pageable pageable) {
        log.info("Buscando transacciones con filtros: estado={}, diaMesPago={}, pais={}, incluirEliminadas={}", 
                estado, diaMesPago, pais, incluirEliminadas);
                
        validarParametrosFiltro(estado);
        
        return this.repository.buscarConFiltros(estado, diaMesPago, pais, incluirEliminadas, pageable);
    }

    public TransaccionRecurrente obtenerPorCodigo(String codigo) {
        log.info("Buscando transacción recurrente con código: {}", codigo);
        return this.repository.findById(codigo)
                .orElseThrow(() -> new TransaccionRecurrenteNotFoundException(codigo));
    }

    public List<TransaccionRecurrente> obtenerPorCuentaIban(String cuentaIban) {
        log.info("Buscando transacciones recurrentes para la cuenta IBAN: {}", cuentaIban);
        return this.repository.findByCuentaIbanAndEstado(cuentaIban, "ACT");
    }

    @Transactional
    public TransaccionRecurrente crear(TransaccionRecurrente transaccion) {
        log.info("Se ha creado la transacción");
        validarTransaccion(transaccion);
        transaccion.setCodigo(UUID.randomUUID().toString().substring(0, 10));
        transaccion.setEstado("ACT");
        return this.repository.save(transaccion);
    }

    @Transactional
    public List<TransaccionRecurrente> obtenerTransaccionesParaEjecutar(Integer diaPago) {
        log.info("Buscando transacciones recurrentes para ejecutar en el día: {}", diaPago);
        return this.repository.findByEstadoAndDiaMesPagoAndFechaFinGreaterThanEqual("ACT", diaPago, LocalDate.now());
    }

    @Transactional
    public TransaccionRecurrente actualizarDespuesDeEjecucion(String codigo) {
        log.info("Actualizando transacción recurrente después de la ejecución: {}", codigo);
        TransaccionRecurrente transaccion = obtenerPorCodigo(codigo);
        // No necesitamos actualizar nada adicional en este caso, solo registrar que se ejecutó correctamente
        return transaccion;
    }
    
    public List<TransaccionRecurrente> obtenerPorDiaMes(Integer diaMes) {
        log.info("Buscando transacciones recurrentes para el día del mes: {}", diaMes);
        if (diaMes < 1 || diaMes > 31) {
            throw new TransaccionRecurrenteInvalidaException("El día del mes debe estar entre 1 y 31");
        }
        return this.repository.findByDiaMesPago(diaMes);
    }
    
    public List<TransaccionRecurrente> obtenerPorEstado(String estado) {
        log.info("Buscando transacciones recurrentes con estado: {}", estado);
        if (estado == null || estado.isEmpty()) {
            throw new TransaccionRecurrenteInvalidaException("El estado no puede estar vacío");
        }
        if (!estado.equals("ACT") && !estado.equals("INA") && !estado.equals("ELI")) {
            throw new TransaccionRecurrenteInvalidaException("El estado debe ser ACT, INA o ELI");
        }
        return this.repository.findByEstado(estado);
    }

    private void validarTransaccion(TransaccionRecurrente transaccion) {
        if (transaccion.getFechaInicio() == null) {
            throw new TransaccionRecurrenteInvalidaException("La fecha de inicio es requerida");
        }
        if (transaccion.getFechaFin() != null && transaccion.getFechaFin().isBefore(transaccion.getFechaInicio())) {
            throw new TransaccionRecurrenteInvalidaException("La fecha fin debe ser posterior a la fecha de inicio");
        }
        if (transaccion.getDiaMesPago() < 1 || transaccion.getDiaMesPago() > 31) {
            throw new TransaccionRecurrenteInvalidaException("El día de pago debe estar entre 1 y 31");
        }
        if (transaccion.getFechaCaducidad() != null && transaccion.getFechaCaducidad().isBefore(LocalDate.now())) {
            throw new TransaccionRecurrenteInvalidaException("La fecha de caducidad debe ser futura");
        }
        if (transaccion.getMonto() == null) {
            throw new TransaccionRecurrenteInvalidaException("El monto es requerido");
        }
        if (transaccion.getMonto().compareTo(new java.math.BigDecimal("100000.00")) > 0) {
            throw new TransaccionRecurrenteInvalidaException("El monto máximo permitido es de 100000 dólares");
        }
    }

    private void validarParametrosFiltro(String estado) {
        if (estado != null && !estado.isEmpty() && 
            !estado.equals("ACT") && !estado.equals("INA") && !estado.equals("ELI")) {
            throw new TransaccionRecurrenteInvalidaException("El estado debe ser ACT, INA o ELI");
        }
    }
} 