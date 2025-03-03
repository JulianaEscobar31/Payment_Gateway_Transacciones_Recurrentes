package com.banquito.gateway.transaccionrecurrente.banquito.client.mapper;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.banquito.gateway.transaccionrecurrente.banquito.client.dto.TransaccionSimpleDTO;
import com.banquito.gateway.transaccionrecurrente.banquito.model.TransaccionRecurrente;

@Component
public class TransaccionSimpleMapper {
    
    public TransaccionSimpleDTO toTransaccionSimpleDTO(TransaccionRecurrente transaccion) {
        if (transaccion == null) {
            return null;
        }
        
        TransaccionSimpleDTO dto = new TransaccionSimpleDTO();
        dto.setCodTransaccion(UUID.randomUUID().toString().substring(0, 10));
        dto.setTipo("REC"); 
        dto.setMarca(transaccion.getMarca());
        dto.setMonto(transaccion.getMonto());
        dto.setCodigoUnicoTransaccion(UUID.randomUUID().toString());
        dto.setFecha(LocalDateTime.now());
        dto.setEstado("PEN"); 
        dto.setMoneda(transaccion.getMoneda());
        dto.setPais(transaccion.getPais());
        
        if (transaccion.getTarjeta() != null) {
            dto.setNumeroTarjeta(transaccion.getTarjeta().toString());
        }
        
        if (transaccion.getFechaCaducidad() != null) {
            String mes = String.format("%02d", transaccion.getFechaCaducidad().getMonthValue());
            String anio = String.valueOf(transaccion.getFechaCaducidad().getYear() % 100);
            dto.setFechaExpiracion(mes + "/" + anio);
        }
        
        dto.setSwift_banco(transaccion.getSwiftBanco());
        dto.setCuenta_iban(transaccion.getCuentaIban());
        
        if (transaccion.getCvv() != null && !transaccion.getCvv().isEmpty()) {
            try {
                dto.setCvv(Integer.parseInt(transaccion.getCvv()));
            } catch (NumberFormatException e) {
                dto.setCvv(123);
            }
        }
        
        dto.setFrecuenciaDias(transaccion.getFrecuenciaDias());
        dto.setDiferido(false);
        
        return dto;
    }
} 