package com.banquito.gateway.transaccionrecurrente.banquito.client.dto;

import java.math.BigDecimal;
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
    
    @JsonProperty("cvv")
    private Integer cvv;
    
    @JsonProperty("frecuenciaDias")
    private Integer frecuenciaDias;
    
    @JsonProperty("codTransaccion")
    private String codTransaccion;
    
    @JsonProperty("tipo")
    private String tipo;
    
    @JsonProperty("codigoUnicoTransaccion")
    private String codigoUnicoTransaccion;
    
    @JsonProperty("fecha")
    private LocalDateTime fecha;
    
    @JsonProperty("estado")
    private String estado;
    
    @JsonProperty("diferido")
    private Boolean diferido;
} 