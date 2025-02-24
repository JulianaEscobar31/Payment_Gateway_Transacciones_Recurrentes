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
    public TransaccionRecurrente actualizar(String codigo, TransaccionRecurrente transaccion) {
        log.info("Actualizando transacción recurrente con código: {}", codigo);
        TransaccionRecurrente transaccionExistente = obtenerPorCodigo(codigo);
        validarTransaccion(transaccion);
        
        transaccionExistente.setMonto(transaccion.getMonto());
        transaccionExistente.setMarca(transaccion.getMarca());
        transaccionExistente.setFechaInicio(transaccion.getFechaInicio());
        transaccionExistente.setFechaFin(transaccion.getFechaFin());
        transaccionExistente.setDiaMesPago(transaccion.getDiaMesPago());
        transaccionExistente.setSwiftBanco(transaccion.getSwiftBanco());
        transaccionExistente.setCuentaIban(transaccion.getCuentaIban());
        transaccionExistente.setMoneda(transaccion.getMoneda());
        transaccionExistente.setPais(transaccion.getPais());
        transaccionExistente.setTarjeta(transaccion.getTarjeta());
        transaccionExistente.setFechaCaducidad(transaccion.getFechaCaducidad());

        return this.repository.save(transaccionExistente);
    }

    @Transactional
    public void eliminar(String codigo) {
        log.info("Eliminando transacción recurrente con código: {}", codigo);
        TransaccionRecurrente transaccion = obtenerPorCodigo(codigo);
        transaccion.setEstado("ELI");
        this.repository.save(transaccion);
    }

    @Transactional
    public List<TransaccionRecurrente> obtenerTransaccionesParaEjecutar(Integer diaPago) {
        log.info("Buscando transacciones recurrentes para ejecutar en el día: {}", diaPago);
        return this.repository.findByEstadoAndDiaMesPagoAndFechaFinGreaterThanEqual("ACT", diaPago, LocalDate.now());
    }

    @Transactional
    public List<TransaccionRecurrente> obtenerTransaccionesVencidas() {
        log.info("Buscando transacciones recurrentes vencidas");
        return this.repository.findByEstadoAndFechaFinLessThanEqual("ACT", LocalDate.now());
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