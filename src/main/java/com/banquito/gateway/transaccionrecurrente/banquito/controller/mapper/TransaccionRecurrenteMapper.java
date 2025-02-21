package com.banquito.gateway.transaccionrecurrente.banquito.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.banquito.gateway.transaccionrecurrente.banquito.controller.dto.TransaccionRecurrenteDTO;
import com.banquito.gateway.transaccionrecurrente.banquito.model.TransaccionRecurrente;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TransaccionRecurrenteMapper {
    
    TransaccionRecurrenteDTO toDTO(TransaccionRecurrente model);
    
    TransaccionRecurrente toModel(TransaccionRecurrenteDTO dto);
} 