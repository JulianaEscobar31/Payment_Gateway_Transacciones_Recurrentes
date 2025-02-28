package com.banquito.gateway.transaccionrecurrente.banquito.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.banquito.gateway.transaccionrecurrente.banquito.controller.dto.TransaccionRecurrenteDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TransaccionRecurrenteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    public void contextLoads() {
    }

    @Test
    public void testObtenerTodas() throws Exception {
        mockMvc.perform(get("/v1/transacciones-recurrentes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testCrearTransaccion() throws Exception {
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

        mockMvc.perform(post("/v1/transacciones-recurrentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevaTransaccionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").exists())
                .andExpect(jsonPath("$.estado").value("ACT"));
    }
} 