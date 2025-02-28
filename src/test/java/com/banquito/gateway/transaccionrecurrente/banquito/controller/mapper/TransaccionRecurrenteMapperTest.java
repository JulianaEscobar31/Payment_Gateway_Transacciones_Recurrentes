package com.banquito.gateway.transaccionrecurrente.banquito.controller.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.banquito.gateway.transaccionrecurrente.banquito.controller.dto.TransaccionRecurrenteDTO;
import com.banquito.gateway.transaccionrecurrente.banquito.model.TransaccionRecurrente;

@SpringBootTest
public class TransaccionRecurrenteMapperTest {

    @Autowired
    private TransaccionRecurrenteMapper mapper;

    @Test
    public void testToDTO_NullModel() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    public void testToModel_NullDTO() {
        assertNull(mapper.toModel(null));
    }

    @Test
    public void testToDTO() {
        LocalDate hoy = LocalDate.now();
        LocalDate futuro = hoy.plusYears(2);
        
        TransaccionRecurrente model = new TransaccionRecurrente();
        model.setCodigo("TR12345678");
        model.setMonto(new BigDecimal("100.50"));
        model.setMarca("VISA");
        model.setEstado("ACT");
        model.setFechaInicio(hoy);
        model.setFechaFin(futuro);
        model.setDiaMesPago(15);
        model.setSwiftBanco("BSCHESMM");
        model.setCuentaIban("ES9121000418450200051332");
        model.setMoneda("EUR");
        model.setPais("ES");
        model.setTarjeta(4532123456789012L);
        model.setFechaCaducidad(futuro);

        TransaccionRecurrenteDTO dto = mapper.toDTO(model);

        assertEquals("TR12345678", dto.getCodigo());
        assertEquals(new BigDecimal("100.50"), dto.getMonto());
        assertEquals("VISA", dto.getMarca());
        assertEquals("ACT", dto.getEstado());
        assertEquals(hoy, dto.getFechaInicio());
        assertEquals(futuro, dto.getFechaFin());
        assertEquals(Integer.valueOf(15), dto.getDiaMesPago());
        assertEquals("BSCHESMM", dto.getSwiftBanco());
        assertEquals("ES9121000418450200051332", dto.getCuentaIban());
        assertEquals("EUR", dto.getMoneda());
        assertEquals("ES", dto.getPais());
        assertEquals(Long.valueOf(4532123456789012L), dto.getTarjeta());
        assertEquals(futuro, dto.getFechaCaducidad());
    }

    @Test
    public void testToModel() {
        LocalDate hoy = LocalDate.now();
        LocalDate futuro = hoy.plusYears(2);
        
        TransaccionRecurrenteDTO dto = new TransaccionRecurrenteDTO();
        dto.setCodigo("TR12345678");
        dto.setMonto(new BigDecimal("100.50"));
        dto.setMarca("VISA");
        dto.setEstado("ACT");
        dto.setFechaInicio(hoy);
        dto.setFechaFin(futuro);
        dto.setDiaMesPago(15);
        dto.setSwiftBanco("BSCHESMM");
        dto.setCuentaIban("ES9121000418450200051332");
        dto.setMoneda("EUR");
        dto.setPais("ES");
        dto.setTarjeta(4532123456789012L);
        dto.setFechaCaducidad(futuro);

        TransaccionRecurrente model = mapper.toModel(dto);

        assertEquals("TR12345678", model.getCodigo());
        assertEquals(new BigDecimal("100.50"), model.getMonto());
        assertEquals("VISA", model.getMarca());
        assertEquals("ACT", model.getEstado());
        assertEquals(hoy, model.getFechaInicio());
        assertEquals(futuro, model.getFechaFin());
        assertEquals(Integer.valueOf(15), model.getDiaMesPago());
        assertEquals("BSCHESMM", model.getSwiftBanco());
        assertEquals("ES9121000418450200051332", model.getCuentaIban());
        assertEquals("EUR", model.getMoneda());
        assertEquals("ES", model.getPais());
        assertEquals(Long.valueOf(4532123456789012L), model.getTarjeta());
        assertEquals(futuro, model.getFechaCaducidad());
    }
} 