package com.banquito.gateway.transaccionrecurrente.banquito.controller.mapper;

import org.springframework.stereotype.Component;

import com.banquito.gateway.transaccionrecurrente.banquito.controller.dto.TransaccionRecurrenteDTO;
import com.banquito.gateway.transaccionrecurrente.banquito.model.TransaccionRecurrente;

@Component
public class TransaccionRecurrenteMapper {
    
    public TransaccionRecurrenteDTO toDTO(TransaccionRecurrente model) {
        if (model == null) {
            return null;
        }

        TransaccionRecurrenteDTO dto = new TransaccionRecurrenteDTO();
        dto.setCodigo(model.getCodigo());
        dto.setTipo(model.getTipo());
        dto.setMontoBase(model.getMontoBase());
        dto.setTransaccionesBase(model.getTransaccionesBase());
        dto.setManejaSegmentos(model.getManejaSegmentos());
        return dto;
    }
    
    public TransaccionRecurrente toModel(TransaccionRecurrenteDTO dto) {
        if (dto == null) {
            return null;
        }

        TransaccionRecurrente model = new TransaccionRecurrente();
        model.setCodigo(dto.getCodigo());
        model.setTipo(dto.getTipo());
        model.setMontoBase(dto.getMontoBase());
        model.setTransaccionesBase(dto.getTransaccionesBase());
        model.setManejaSegmentos(dto.getManejaSegmentos());
        return model;
    }
} 