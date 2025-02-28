package com.banquito.gateway.transaccionrecurrente.banquito.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.banquito.gateway.transaccionrecurrente.banquito.exception.TransaccionRecurrenteInvalidaException;
import com.banquito.gateway.transaccionrecurrente.banquito.exception.TransaccionRecurrenteNotFoundException;
import com.banquito.gateway.transaccionrecurrente.banquito.model.TransaccionRecurrente;
import com.banquito.gateway.transaccionrecurrente.banquito.repository.TransaccionRecurrenteRepository;

@ExtendWith(MockitoExtension.class)
public class TransaccionRecurrenteServiceTest {

    @Mock
    private TransaccionRecurrenteRepository repository;

    @InjectMocks
    private TransaccionRecurrenteService service;

    private TransaccionRecurrente transaccionRecurrente;
    private List<TransaccionRecurrente> transaccionesList;

    @BeforeEach
    public void setup() {
        transaccionRecurrente = new TransaccionRecurrente();
        transaccionRecurrente.setCodigo("TR12345678");
        transaccionRecurrente.setMonto(new BigDecimal("100.50"));
        transaccionRecurrente.setMarca("VISA");
        transaccionRecurrente.setEstado("ACT");
        transaccionRecurrente.setFechaInicio(LocalDate.now());
        transaccionRecurrente.setFechaFin(LocalDate.now().plusMonths(12));
        transaccionRecurrente.setDiaMesPago(15);
        transaccionRecurrente.setSwiftBanco("BSCHESMM");
        transaccionRecurrente.setCuentaIban("ES9121000418450200051332");
        transaccionRecurrente.setMoneda("EUR");
        transaccionRecurrente.setPais("ES");
        transaccionRecurrente.setTarjeta(4532123456789012L);
        transaccionRecurrente.setFechaCaducidad(LocalDate.now().plusYears(3));

        TransaccionRecurrente transaccion2 = new TransaccionRecurrente();
        transaccion2.setCodigo("TR87654321");
        transaccion2.setMonto(new BigDecimal("250.75"));
        transaccion2.setMarca("MAST");
        transaccion2.setEstado("ACT");
        transaccion2.setFechaInicio(LocalDate.now());
        transaccion2.setFechaFin(LocalDate.now().plusMonths(6));
        transaccion2.setDiaMesPago(5);
        transaccion2.setSwiftBanco("BSCHESMM");
        transaccion2.setCuentaIban("ES9121000418450200051332");
        transaccion2.setMoneda("EUR");
        transaccion2.setPais("ES");
        transaccion2.setTarjeta(5555555555554444L);
        transaccion2.setFechaCaducidad(LocalDate.now().plusYears(2));

        transaccionesList = Arrays.asList(transaccionRecurrente, transaccion2);
    }

    @Test
    public void testObtenerTodas() {
        when(repository.findAll()).thenReturn(transaccionesList);

        List<TransaccionRecurrente> resultado = service.obtenerTodas();

        verify(repository, times(1)).findAll();
        assertEquals(2, resultado.size());
        assertEquals("TR12345678", resultado.get(0).getCodigo());
        assertEquals("TR87654321", resultado.get(1).getCodigo());
    }

    @Test
    public void testObtenerPorCodigo_ExisteTransaccion() {
        when(repository.findById(anyString())).thenReturn(Optional.of(transaccionRecurrente));

        TransaccionRecurrente resultado = service.obtenerPorCodigo("TR12345678");

        verify(repository, times(1)).findById("TR12345678");
        assertEquals("TR12345678", resultado.getCodigo());
        assertEquals(new BigDecimal("100.50"), resultado.getMonto());
        assertEquals("VISA", resultado.getMarca());
    }

    @Test
    public void testObtenerPorCodigo_NoExisteTransaccion() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(TransaccionRecurrenteNotFoundException.class, () -> {
            service.obtenerPorCodigo("TR99999999");
        });

        verify(repository, times(1)).findById("TR99999999");
    }

    @Test
    public void testObtenerPorTarjeta() {
        when(repository.findByTarjetaAndEstado(anyLong(), anyString())).thenReturn(transaccionesList);

        List<TransaccionRecurrente> resultado = service.obtenerPorTarjeta(4532123456789012L);

        verify(repository, times(1)).findByTarjetaAndEstado(4532123456789012L, "ACT");
        assertEquals(2, resultado.size());
    }

    @Test
    public void testObtenerPorCuentaIban() {
        when(repository.findByCuentaIbanAndEstado(anyString(), anyString())).thenReturn(transaccionesList);

        List<TransaccionRecurrente> resultado = service.obtenerPorCuentaIban("ES9121000418450200051332");

        verify(repository, times(1)).findByCuentaIbanAndEstado("ES9121000418450200051332", "ACT");
        assertEquals(2, resultado.size());
    }

    @Test
    public void testCrear() {
        TransaccionRecurrente nuevaTransaccion = new TransaccionRecurrente();
        nuevaTransaccion.setMonto(new BigDecimal("150.75"));
        nuevaTransaccion.setMarca("VISA");
        nuevaTransaccion.setFechaInicio(LocalDate.now());
        nuevaTransaccion.setDiaMesPago(15);
        nuevaTransaccion.setSwiftBanco("BSCHESMM");
        nuevaTransaccion.setCuentaIban("ES9121000418450200051332");
        nuevaTransaccion.setMoneda("EUR");
        nuevaTransaccion.setPais("ES");
        nuevaTransaccion.setTarjeta(4532123456789012L);
        nuevaTransaccion.setFechaCaducidad(LocalDate.now().plusYears(2));

        when(repository.save(any(TransaccionRecurrente.class))).thenReturn(nuevaTransaccion);

        TransaccionRecurrente resultado = service.crear(nuevaTransaccion);

        verify(repository, times(1)).save(any(TransaccionRecurrente.class));
        assertNotNull(resultado.getCodigo());
        assertEquals("ACT", resultado.getEstado());
        assertEquals(new BigDecimal("150.75"), resultado.getMonto());
    }

    @Test
    public void testCrear_FechaInicioNull() {
        TransaccionRecurrente transaccionInvalida = new TransaccionRecurrente();
        transaccionInvalida.setMonto(new BigDecimal("150.75"));
        transaccionInvalida.setMarca("VISA");
        transaccionInvalida.setDiaMesPago(15);
        transaccionInvalida.setSwiftBanco("BSCHESMM");
        transaccionInvalida.setCuentaIban("ES9121000418450200051332");
        transaccionInvalida.setMoneda("EUR");
        transaccionInvalida.setPais("ES");
        transaccionInvalida.setTarjeta(4532123456789012L);
        transaccionInvalida.setFechaCaducidad(LocalDate.now().plusYears(2));

        assertThrows(TransaccionRecurrenteInvalidaException.class, () -> {
            service.crear(transaccionInvalida);
        });
    }

    @Test
    public void testCrear_FechaFinAnteriorAFechaInicio() {
        TransaccionRecurrente transaccionInvalida = new TransaccionRecurrente();
        transaccionInvalida.setMonto(new BigDecimal("150.75"));
        transaccionInvalida.setMarca("VISA");
        transaccionInvalida.setFechaInicio(LocalDate.now());
        transaccionInvalida.setFechaFin(LocalDate.now().minusDays(1));
        transaccionInvalida.setDiaMesPago(15);
        transaccionInvalida.setSwiftBanco("BSCHESMM");
        transaccionInvalida.setCuentaIban("ES9121000418450200051332");
        transaccionInvalida.setMoneda("EUR");
        transaccionInvalida.setPais("ES");
        transaccionInvalida.setTarjeta(4532123456789012L);
        transaccionInvalida.setFechaCaducidad(LocalDate.now().plusYears(2));

        assertThrows(TransaccionRecurrenteInvalidaException.class, () -> {
            service.crear(transaccionInvalida);
        });
    }

    @Test
    public void testObtenerPorDiaMes_DiaMesValido() {
        when(repository.findByDiaMesPago(anyInt())).thenReturn(transaccionesList);

        List<TransaccionRecurrente> resultado = service.obtenerPorDiaMes(15);

        verify(repository, times(1)).findByDiaMesPago(15);
        assertEquals(2, resultado.size());
    }

    @Test
    public void testObtenerPorDiaMes_DiaMesInvalido() {
        assertThrows(TransaccionRecurrenteInvalidaException.class, () -> {
            service.obtenerPorDiaMes(32);
        });
    }

    @Test
    public void testObtenerPorEstado_EstadoValido() {
        when(repository.findByEstado(anyString())).thenReturn(transaccionesList);

        List<TransaccionRecurrente> resultado = service.obtenerPorEstado("ACT");

        verify(repository, times(1)).findByEstado("ACT");
        assertEquals(2, resultado.size());
    }

    @Test
    public void testObtenerPorEstado_EstadoInvalido() {
        assertThrows(TransaccionRecurrenteInvalidaException.class, () -> {
            service.obtenerPorEstado("XXX");
        });
    }

    @Test
    public void testObtenerPorEstado_EstadoVacio() {
        assertThrows(TransaccionRecurrenteInvalidaException.class, () -> {
            service.obtenerPorEstado("");
        });
    }

    @Test
    public void testObtenerTransaccionesParaEjecutar() {
        when(repository.findByEstadoAndDiaMesPagoAndFechaFinGreaterThanEqual(anyString(), anyInt(), any(LocalDate.class)))
                .thenReturn(transaccionesList);

        List<TransaccionRecurrente> resultado = service.obtenerTransaccionesParaEjecutar(15);

        verify(repository, times(1)).findByEstadoAndDiaMesPagoAndFechaFinGreaterThanEqual(
                eq("ACT"), eq(15), any(LocalDate.class));
        assertEquals(2, resultado.size());
    }
} 