package com.banquito.gateway.transaccionrecurrente.banquito.controller.mapper;

import java.util.List;
import java.util.ArrayList;

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
        dto.setCvv(model.getCvv());
        dto.setFrecuenciaDias(model.getFrecuenciaDias());
        
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
        model.setCvv(dto.getCvv());
        model.setFrecuenciaDias(dto.getFrecuenciaDias());
        
        return model;
    }
    
    public List<TransaccionRecurrenteDTO> toDTOList(List<TransaccionRecurrente> models) {
        if (models == null) {
            return null;
        }
        
        List<TransaccionRecurrenteDTO> dtos = new ArrayList<>(models.size());
        for (TransaccionRecurrente model : models) {
            dtos.add(toDTO(model));
        }
        
        return dtos;
    }
    
    public List<TransaccionRecurrente> toModelList(List<TransaccionRecurrenteDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        
        List<TransaccionRecurrente> models = new ArrayList<>(dtos.size());
        for (TransaccionRecurrenteDTO dto : dtos) {
            models.add(toModel(dto));
        }
        
        return models;
    }
} 