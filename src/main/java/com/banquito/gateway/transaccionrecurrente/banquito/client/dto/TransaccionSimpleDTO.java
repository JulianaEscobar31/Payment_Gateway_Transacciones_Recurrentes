package com.banquito.gateway.transaccionrecurrente.banquito.client.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransaccionSimpleDTO {
    
    private String codigoTransaccion;
    private String codigoReferencia;
    private BigDecimal monto;
    private String moneda;
    private String descripcion;
    private String cuentaOrigen;
    private String cuentaDestino;
    private String tipoTransaccion;
    private String estado;
    private LocalDateTime fechaEjecucion;
    private String canal;
    private String marca;
    private String pais;
    private Long tarjeta;
} 