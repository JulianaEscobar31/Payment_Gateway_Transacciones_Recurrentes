package com.banquito.gateway.transaccionrecurrente.banquito.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "transaccion_recurrente")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class TransaccionRecurrente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "codigo", nullable = false)
    private String codigo;

    @Column(name = "tipo", nullable = false, length = 3)
    private String tipo;

    @Column(name = "monto_base", nullable = false, precision = 18, scale = 4)
    private BigDecimal montoBase;

    @Column(name = "transacciones_base", nullable = false)
    private Integer transaccionesBase;

    @Column(name = "maneja_segmentos", nullable = false)
    private Boolean manejaSegmentos;

    @Column(name = "MONTO", precision = 20, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "MARCA", length = 4)
    private String marca;

    @Column(name = "ESTADO", length = 3)
    private String estado;

    @Column(name = "FECHA_INICIO")
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN")
    private LocalDate fechaFin;

    @Column(name = "DIA_MES_PAGO", precision = 2)
    private Integer diaMesPago;

    @Column(name = "SWIFT_BANCO", length = 11)
    private String swiftBanco;

    @Column(name = "CUENTA_IBAN", length = 28)
    private String cuentaIban;

    @Column(name = "MONEDA", length = 3)
    private String moneda;

    @Column(name = "PAIS", length = 2)
    private String pais;

    @Column(name = "TARJETA", precision = 16)
    private Long tarjeta;

    @Column(name = "FECHA_CADUCIDAD")
    private LocalDate fechaCaducidad;

    public TransaccionRecurrente(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TransaccionRecurrente other = (TransaccionRecurrente) obj;
        if (codigo == null) {
            if (other.codigo != null)
                return false;
        } else if (!codigo.equals(other.codigo))
            return false;
        return true;
    }
} 