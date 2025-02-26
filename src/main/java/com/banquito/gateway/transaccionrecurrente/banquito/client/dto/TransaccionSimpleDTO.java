package com.banquito.gateway.transaccionrecurrente.banquito.client.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransaccionSimpleDTO {
    
    private String codTransaccion;
    private String tipo;
    private String marca;
    private BigDecimal monto;
    private String codUnicoTransaccion;
    private LocalDateTime fecha;
    private String estado;
    private String moneda;
    private String pais;
    private BigDecimal tarjeta;
    private LocalDateTime fechaCaducidad;
    private String transaccionEncriptada;
    private String swiftBanco;
    private String cuentaIban;
    private Boolean diferido;
} 