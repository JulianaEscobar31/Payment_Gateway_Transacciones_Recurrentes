package com.banquito.gateway.transaccionrecurrente.banquito.client.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransaccionSimpleDTO {
    
    private String marca;
    private BigDecimal monto;
    private String moneda;
    private String pais;
    
    @JsonProperty("numeroTarjeta")
    private String numeroTarjeta;
    
    @JsonProperty("fechaExpiracion")
    private String fechaExpiracion;
    
    @JsonProperty("swift_banco")
    private String swift_banco;
    
    @JsonProperty("cuenta_iban")
    private String cuenta_iban;
    
    private Integer cvv;
    private Integer frecuenciaDias;
    
    private String codTransaccion;
    private String tipo;
    private String codigoUnicoTransaccion;
    private LocalDateTime fecha;
    private String estado;
    private Boolean diferido;
} 