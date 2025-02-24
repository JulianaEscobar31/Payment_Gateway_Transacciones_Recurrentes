package com.banquito.gateway.transaccionrecurrente.banquito.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransaccionRecurrenteDTO {
    
    private String codigo;

    @NotBlank(message = "El tipo es requerido")
    @Pattern(regexp = "POR|FIJ", message = "El tipo debe ser POR o FIJ")
    private String tipo;

    @NotNull(message = "El monto base es requerido")
    @DecimalMin(value = "0.0", message = "El monto base no puede ser negativo")
    @DecimalMax(value = "999999999.9999", message = "El monto base excede el límite permitido")
    private BigDecimal montoBase;

    @NotNull(message = "El número de transacciones base es requerido")
    @Min(value = 0, message = "El número de transacciones base no puede ser negativo")
    private Integer transaccionesBase;

    @NotNull(message = "El campo maneja segmentos es requerido")
    private Boolean manejaSegmentos;

    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @NotBlank(message = "La marca es requerida")
    @Size(min = 1, max = 4, message = "La marca debe tener entre 1 y 4 caracteres")
    private String marca;

    @Pattern(regexp = "ACT|INA|ELI", message = "El estado debe ser ACT, INA o ELI")
    private String estado;

    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDate fechaInicio;

    @Future(message = "La fecha fin debe ser futura")
    private LocalDate fechaFin;

    @NotNull(message = "El día de pago es requerido")
    @Min(value = 1, message = "El día de pago debe ser entre 1 y 31")
    @Max(value = 31, message = "El día de pago debe ser entre 1 y 31")
    private Integer diaMesPago;

    @NotBlank(message = "El SWIFT del banco es requerido")
    @Size(min = 8, max = 11, message = "El SWIFT debe tener entre 8 y 11 caracteres")
    private String swiftBanco;

    @NotBlank(message = "La cuenta IBAN es requerida")
    @Size(min = 15, max = 28, message = "El IBAN debe tener entre 15 y 28 caracteres")
    private String cuentaIban;

    @NotBlank(message = "La moneda es requerida")
    @Size(min = 3, max = 3, message = "La moneda debe tener 3 caracteres")
    private String moneda;

    @NotBlank(message = "El país es requerido")
    @Size(min = 2, max = 2, message = "El país debe tener 2 caracteres")
    private String pais;

    @NotNull(message = "El número de tarjeta es requerido")
    private Long tarjeta;

    @NotNull(message = "La fecha de caducidad es requerida")
    @Future(message = "La fecha de caducidad debe ser futura")
    private LocalDate fechaCaducidad;
} 