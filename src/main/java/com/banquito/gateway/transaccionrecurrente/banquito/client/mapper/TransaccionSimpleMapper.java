package com.banquito.gateway.transaccionrecurrente.banquito.client.mapper;

import java.math.BigDecimal;
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
        dto.setCodUnicoTransaccion(transaccion.getCodigo());
        dto.setFecha(LocalDateTime.now());
        dto.setEstado("PEN"); 
        dto.setMoneda(transaccion.getMoneda());
        dto.setPais(transaccion.getPais());
        dto.setTarjeta(new BigDecimal(transaccion.getTarjeta()));
        dto.setFechaCaducidad(LocalDateTime.of(
                transaccion.getFechaCaducidad().getYear(),
                transaccion.getFechaCaducidad().getMonth(),
                transaccion.getFechaCaducidad().getDayOfMonth(),
                0, 0));
        dto.setSwiftBanco(transaccion.getSwiftBanco());
        dto.setCuentaIban(transaccion.getCuentaIban());
        dto.setDiferido(false);
        
        return dto;
    }
} 