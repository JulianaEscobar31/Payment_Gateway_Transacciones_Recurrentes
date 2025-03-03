package com.banquito.gateway.transaccionrecurrente.banquito.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.banquito.gateway.transaccionrecurrente.banquito.client.dto.TransaccionSimpleDTO;

@FeignClient(name = "transaccion-simple", url ="https://94ca-2800-370-d3-b1b0-2d0c-38fe-d1a7-d741.ngrok-free.app")
public interface TransaccionSimpleClient {
     
    @PostMapping("/api/v1/transacciones/recurrentes")
    ResponseEntity<TransaccionSimpleDTO> ejecutarTransaccion(@RequestBody TransaccionSimpleDTO transaccion);
} 