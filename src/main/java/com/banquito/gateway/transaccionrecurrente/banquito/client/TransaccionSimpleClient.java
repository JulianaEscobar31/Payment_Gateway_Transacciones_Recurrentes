package com.banquito.gateway.transaccionrecurrente.banquito.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.banquito.gateway.transaccionrecurrente.banquito.client.dto.TransaccionSimpleDTO;

@FeignClient(name = "transaccion-simple", url = "${transaccion.simple.url:http://localhost:8081}")
public interface TransaccionSimpleClient {
    
    @PostMapping("/v1/transaccionessimples")
    ResponseEntity<TransaccionSimpleDTO> ejecutarTransaccion(@RequestBody TransaccionSimpleDTO transaccion);
} 