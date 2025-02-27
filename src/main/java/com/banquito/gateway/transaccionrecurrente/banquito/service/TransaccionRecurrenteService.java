package com.banquito.gateway.transaccionrecurrente.banquito.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public TransaccionRecurrente obtenerPorCodigo(String codigo) {
        log.info("Buscando transacción recurrente con código: {}", codigo);
        return this.repository.findById(codigo)
                .orElseThrow(() -> new TransaccionRecurrenteNotFoundException(codigo));
    }

    public List<TransaccionRecurrente> obtenerPorTarjeta(Long tarjeta) {
        log.info("Buscando transacciones recurrentes para la tarjeta: {}", tarjeta);
        return this.repository.findByTarjetaAndEstado(tarjeta, "ACT");
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
    }
} 