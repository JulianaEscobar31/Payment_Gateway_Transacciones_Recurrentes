package com.banquito.gateway.transaccionrecurrente.banquito.client.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransaccionSimpleRequestDTO {
    
    private String codigo;
    private String tipo;
    private BigDecimal monto;
    private String fecha;
    private String fechaHoraTransaccion;
    private String estado;
    private String pais;
    private Long tarjeta;
    private String fechaCaducidad;
    private String swiftBanco;
    private String cuentaIban;
    private String moneda;
    private String transaccionEncriptada;
    private String cvv;
    private String banco;
    private String diferido;
    
} 