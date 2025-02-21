package com.banquito.gateway.transaccionrecurrente.banquito.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.gateway.transaccionrecurrente.banquito.model.TransaccionRecurrente;

@Repository
public interface TransaccionRecurrenteRepository extends JpaRepository<TransaccionRecurrente, String> {
    
    List<TransaccionRecurrente> findByEstadoAndDiaMesPagoAndFechaFinGreaterThanEqual(String estado, Integer diaMesPago, LocalDate fechaFin);
    
    List<TransaccionRecurrente> findByEstadoAndFechaFinLessThanEqual(String estado, LocalDate fechaFin);
    
    List<TransaccionRecurrente> findByTarjetaAndEstado(Long tarjeta, String estado);
    
    List<TransaccionRecurrente> findByCuentaIbanAndEstado(String cuentaIban, String estado);
} 