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
        dto.setMonto(model.getMonto());
        dto.setMarca(model.getMarca());
        dto.setEstado(model.getEstado());
        dto.setFechaInicio(model.getFechaInicio());
        dto.setFechaFin(model.getFechaFin());
        dto.setDiaMesPago(model.getDiaMesPago());
        dto.setSwiftBanco(model.getSwiftBanco());
        dto.setCuentaIban(model.getCuentaIban());
        dto.setMoneda(model.getMoneda());
        dto.setPais(model.getPais());
        dto.setTarjeta(model.getTarjeta());
        dto.setFechaCaducidad(model.getFechaCaducidad());
        return dto;
    }
    
    public TransaccionRecurrente toModel(TransaccionRecurrenteDTO dto) {
        if (dto == null) {
            return null;
        }

        TransaccionRecurrente model = new TransaccionRecurrente();
        model.setCodigo(dto.getCodigo());
        model.setMonto(dto.getMonto());
        model.setMarca(dto.getMarca());
        model.setEstado(dto.getEstado());
        model.setFechaInicio(dto.getFechaInicio());
        model.setFechaFin(dto.getFechaFin());
        model.setDiaMesPago(dto.getDiaMesPago());
        model.setSwiftBanco(dto.getSwiftBanco());
        model.setCuentaIban(dto.getCuentaIban());
        model.setMoneda(dto.getMoneda());
        model.setPais(dto.getPais());
        model.setTarjeta(dto.getTarjeta());
        model.setFechaCaducidad(dto.getFechaCaducidad());
        return model;
    }
} 