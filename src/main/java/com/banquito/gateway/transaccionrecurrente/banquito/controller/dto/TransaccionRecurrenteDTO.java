package com.banquito.gateway.transaccionrecurrente.banquito.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransaccionRecurrenteDTO {
    
    @Size(max = 10, message = "El código debe tener máximo 10 caracteres")
    private String codigo;

    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    @DecimalMax(value = "100000.00", message = "El monto máximo permitido es de 100000 dólares")
    private BigDecimal monto;

    @NotBlank(message = "La marca es requerida")
    @Size(min = 1, max = 4, message = "La marca debe tener entre 1 y 4 caracteres")
    private String marca;

    @Pattern(regexp = "ACT|INA", message = "El estado debe ser ACT o INA")
    private String estado;

    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    @NotNull(message = "El día de pago es requerido")
    @DecimalMin(value = "1", message = "El día debe ser mayor a 0")
    @DecimalMax(value = "31", message = "El día no puede ser mayor a 31")
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

    @Size(min = 3, max = 4, message = "El CVV debe tener entre 3 y 4 caracteres")
    private String cvv;
    
    @DecimalMin(value = "1", message = "La frecuencia debe ser mayor a 0")
    private Integer frecuenciaDias;

} 