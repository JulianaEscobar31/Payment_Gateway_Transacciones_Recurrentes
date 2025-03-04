package com.banquito.gateway.transaccionrecurrente.banquito.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.banquito.gateway.transaccionrecurrente.banquito.model.TransaccionRecurrente;

@Repository
public interface TransaccionRecurrenteRepository extends JpaRepository<TransaccionRecurrente, String> {
    
    List<TransaccionRecurrente> findByEstadoAndDiaMesPagoAndFechaFinGreaterThanEqual(String estado, Integer diaMesPago, LocalDate fechaFin);
    
    List<TransaccionRecurrente> findByEstadoAndFechaFinLessThanEqual(String estado, LocalDate fechaFin);
    
    List<TransaccionRecurrente> findByCuentaIbanAndEstado(String cuentaIban, String estado);
    
    List<TransaccionRecurrente> findByCuentaIban(String cuentaIban);
    
    List<TransaccionRecurrente> findByDiaMesPago(Integer diaMesPago);
    
    List<TransaccionRecurrente> findByEstado(String estado);
    
    @Query("SELECT t FROM TransaccionRecurrente t WHERE " +
           "(:estado IS NULL OR t.estado = :estado) AND " +
           "(:diaMesPago IS NULL OR t.diaMesPago = :diaMesPago) AND " +
           "(:pais IS NULL OR t.pais = :pais) AND " +
           "(:incluirEliminadas = true OR t.estado <> 'ELI')")
    Page<TransaccionRecurrente> buscarConFiltros(
        @Param("estado") String estado, 
        @Param("diaMesPago") Integer diaMesPago, 
        @Param("pais") String pais,
        @Param("incluirEliminadas") boolean incluirEliminadas,
        Pageable pageable
    );
} 