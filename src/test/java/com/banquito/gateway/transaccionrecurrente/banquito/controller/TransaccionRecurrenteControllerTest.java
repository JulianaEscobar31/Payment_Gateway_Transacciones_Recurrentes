package com.banquito.gateway.transaccionrecurrente.banquito.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.banquito.gateway.transaccionrecurrente.banquito.controller.dto.TransaccionRecurrenteDTO;
import com.banquito.gateway.transaccionrecurrente.banquito.controller.mapper.TransaccionRecurrenteMapper;
import com.banquito.gateway.transaccionrecurrente.banquito.exception.TransaccionRecurrenteNotFoundException;
import com.banquito.gateway.transaccionrecurrente.banquito.model.TransaccionRecurrente;
import com.banquito.gateway.transaccionrecurrente.banquito.service.TransaccionRecurrenteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(TransaccionRecurrenteController.class)
public class TransaccionRecurrenteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransaccionRecurrenteService service;

    @MockBean
    private TransaccionRecurrenteMapper mapper;

    private ObjectMapper objectMapper;
    private TransaccionRecurrente transaccionRecurrente;
    private TransaccionRecurrenteDTO transaccionRecurrenteDTO;
    private List<TransaccionRecurrente> transaccionesList;
    private List<TransaccionRecurrenteDTO> transaccionesDTOList;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Configurar modelo
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

        transaccionRecurrenteDTO = new TransaccionRecurrenteDTO();
        transaccionRecurrenteDTO.setCodigo("TR12345678");
        transaccionRecurrenteDTO.setMonto(new BigDecimal("100.50"));
        transaccionRecurrenteDTO.setMarca("VISA");
        transaccionRecurrenteDTO.setEstado("ACT");
        transaccionRecurrenteDTO.setFechaInicio(LocalDate.now());
        transaccionRecurrenteDTO.setFechaFin(LocalDate.now().plusMonths(12));
        transaccionRecurrenteDTO.setDiaMesPago(15);
        transaccionRecurrenteDTO.setSwiftBanco("BSCHESMM");
        transaccionRecurrenteDTO.setCuentaIban("ES9121000418450200051332");
        transaccionRecurrenteDTO.setMoneda("EUR");
        transaccionRecurrenteDTO.setPais("ES");
        transaccionRecurrenteDTO.setTarjeta(4532123456789012L);
        transaccionRecurrenteDTO.setFechaCaducidad(LocalDate.now().plusYears(3));

        TransaccionRecurrenteDTO transaccionDTO2 = new TransaccionRecurrenteDTO();
        transaccionDTO2.setCodigo("TR87654321");
        transaccionDTO2.setMonto(new BigDecimal("250.75"));
        transaccionDTO2.setMarca("MAST");
        transaccionDTO2.setEstado("ACT");
        transaccionDTO2.setFechaInicio(LocalDate.now());
        transaccionDTO2.setFechaFin(LocalDate.now().plusMonths(6));
        transaccionDTO2.setDiaMesPago(5);
        transaccionDTO2.setSwiftBanco("BSCHESMM");
        transaccionDTO2.setCuentaIban("ES9121000418450200051332");
        transaccionDTO2.setMoneda("EUR");
        transaccionDTO2.setPais("ES");
        transaccionDTO2.setTarjeta(5555555555554444L);
        transaccionDTO2.setFechaCaducidad(LocalDate.now().plusYears(2));

        transaccionesDTOList = Arrays.asList(transaccionRecurrenteDTO, transaccionDTO2);
    }

    @Test
    public void testObtenerTodas() throws Exception {
        when(service.obtenerTodas()).thenReturn(transaccionesList);
        when(mapper.toDTO(any(TransaccionRecurrente.class))).thenAnswer(invocation -> {
            TransaccionRecurrente tr = invocation.getArgument(0);
            if ("TR12345678".equals(tr.getCodigo())) {
                return transaccionRecurrenteDTO;
            } else {
                return transaccionesDTOList.get(1);
            }
        });

        mockMvc.perform(get("/v1/transacciones-recurrentes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].codigo", is("TR12345678")))
                .andExpect(jsonPath("$[1].codigo", is("TR87654321")));
    }

    @Test
    public void testObtenerPorCodigo_ExisteTransaccion() throws Exception {
        when(service.obtenerPorCodigo(anyString())).thenReturn(transaccionRecurrente);
        when(mapper.toDTO(transaccionRecurrente)).thenReturn(transaccionRecurrenteDTO);

        mockMvc.perform(get("/v1/transacciones-recurrentes/TR12345678")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo", is("TR12345678")))
                .andExpect(jsonPath("$.marca", is("VISA")));
    }

    @Test
    public void testObtenerPorCodigo_NoExisteTransaccion() throws Exception {
        when(service.obtenerPorCodigo(anyString())).thenThrow(new TransaccionRecurrenteNotFoundException("TR99999999"));

        mockMvc.perform(get("/v1/transacciones-recurrentes/TR99999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testObtenerPorTarjeta() throws Exception {
        when(service.obtenerPorTarjeta(anyLong())).thenReturn(transaccionesList);
        when(mapper.toDTO(any(TransaccionRecurrente.class))).thenAnswer(invocation -> {
            TransaccionRecurrente tr = invocation.getArgument(0);
            if ("TR12345678".equals(tr.getCodigo())) {
                return transaccionRecurrenteDTO;
            } else {
                return transaccionesDTOList.get(1);
            }
        });

        mockMvc.perform(get("/v1/transacciones-recurrentes/tarjeta/4532123456789012")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].codigo", is("TR12345678")))
                .andExpect(jsonPath("$[1].codigo", is("TR87654321")));
    }

    @Test
    public void testObtenerPorCuentaIban() throws Exception {
        when(service.obtenerPorCuentaIban(anyString())).thenReturn(transaccionesList);
        when(mapper.toDTO(any(TransaccionRecurrente.class))).thenAnswer(invocation -> {
            TransaccionRecurrente tr = invocation.getArgument(0);
            if ("TR12345678".equals(tr.getCodigo())) {
                return transaccionRecurrenteDTO;
            } else {
                return transaccionesDTOList.get(1);
            }
        });

        mockMvc.perform(get("/v1/transacciones-recurrentes/cuenta/ES9121000418450200051332")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].codigo", is("TR12345678")))
                .andExpect(jsonPath("$[1].codigo", is("TR87654321")));
    }

    @Test
    public void testObtenerPorDiaMes() throws Exception {
        when(service.obtenerPorDiaMes(anyInt())).thenReturn(transaccionesList);
        when(mapper.toDTO(any(TransaccionRecurrente.class))).thenAnswer(invocation -> {
            TransaccionRecurrente tr = invocation.getArgument(0);
            if ("TR12345678".equals(tr.getCodigo())) {
                return transaccionRecurrenteDTO;
            } else {
                return transaccionesDTOList.get(1);
            }
        });

        mockMvc.perform(get("/v1/transacciones-recurrentes/dia-mes/15")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].codigo", is("TR12345678")))
                .andExpect(jsonPath("$[1].codigo", is("TR87654321")));
    }

    @Test
    public void testObtenerPorEstado() throws Exception {
        when(service.obtenerPorEstado(anyString())).thenReturn(transaccionesList);
        when(mapper.toDTO(any(TransaccionRecurrente.class))).thenAnswer(invocation -> {
            TransaccionRecurrente tr = invocation.getArgument(0);
            if ("TR12345678".equals(tr.getCodigo())) {
                return transaccionRecurrenteDTO;
            } else {
                return transaccionesDTOList.get(1);
            }
        });

        mockMvc.perform(get("/v1/transacciones-recurrentes/estado/ACT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].codigo", is("TR12345678")))
                .andExpect(jsonPath("$[1].codigo", is("TR87654321")));
    }

    @Test
    public void testCrear() throws Exception {
        TransaccionRecurrenteDTO nuevaTransaccionDTO = new TransaccionRecurrenteDTO();
        nuevaTransaccionDTO.setMonto(new BigDecimal("150.75"));
        nuevaTransaccionDTO.setMarca("VISA");
        nuevaTransaccionDTO.setFechaInicio(LocalDate.now());
        nuevaTransaccionDTO.setDiaMesPago(15);
        nuevaTransaccionDTO.setSwiftBanco("BSCHESMM");
        nuevaTransaccionDTO.setCuentaIban("ES9121000418450200051332");
        nuevaTransaccionDTO.setMoneda("EUR");
        nuevaTransaccionDTO.setPais("ES");
        nuevaTransaccionDTO.setTarjeta(4532123456789012L);
        nuevaTransaccionDTO.setFechaCaducidad(LocalDate.now().plusYears(2));

        TransaccionRecurrente nuevaTransaccion = new TransaccionRecurrente();
        nuevaTransaccion.setCodigo("TR99999999");
        nuevaTransaccion.setMonto(new BigDecimal("150.75"));
        nuevaTransaccion.setMarca("VISA");
        nuevaTransaccion.setEstado("ACT");
        nuevaTransaccion.setFechaInicio(LocalDate.now());
        nuevaTransaccion.setDiaMesPago(15);
        nuevaTransaccion.setSwiftBanco("BSCHESMM");
        nuevaTransaccion.setCuentaIban("ES9121000418450200051332");
        nuevaTransaccion.setMoneda("EUR");
        nuevaTransaccion.setPais("ES");
        nuevaTransaccion.setTarjeta(4532123456789012L);
        nuevaTransaccion.setFechaCaducidad(LocalDate.now().plusYears(2));

        TransaccionRecurrenteDTO respuestaDTO = new TransaccionRecurrenteDTO();
        respuestaDTO.setCodigo("TR99999999");
        respuestaDTO.setMonto(new BigDecimal("150.75"));
        respuestaDTO.setMarca("VISA");
        respuestaDTO.setEstado("ACT");
        respuestaDTO.setFechaInicio(LocalDate.now());
        respuestaDTO.setDiaMesPago(15);
        respuestaDTO.setSwiftBanco("BSCHESMM");
        respuestaDTO.setCuentaIban("ES9121000418450200051332");
        respuestaDTO.setMoneda("EUR");
        respuestaDTO.setPais("ES");
        respuestaDTO.setTarjeta(4532123456789012L);
        respuestaDTO.setFechaCaducidad(LocalDate.now().plusYears(2));

        when(mapper.toModel(any(TransaccionRecurrenteDTO.class))).thenReturn(nuevaTransaccion);
        when(service.crear(any(TransaccionRecurrente.class))).thenReturn(nuevaTransaccion);
        when(mapper.toDTO(any(TransaccionRecurrente.class))).thenReturn(respuestaDTO);

        mockMvc.perform(post("/v1/transacciones-recurrentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevaTransaccionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo", is("TR99999999")))
                .andExpect(jsonPath("$.estado", is("ACT")));
    }
} 