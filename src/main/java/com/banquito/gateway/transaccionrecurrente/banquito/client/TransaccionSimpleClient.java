package com.banquito.gateway.transaccionrecurrente.banquito.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.banquito.gateway.transaccionrecurrente.banquito.client.dto.TransaccionSimpleDTO;

@FeignClient(name = "transaccion-simple", url = "${feign.client.transaccion-simple.url}")
public interface TransaccionSimpleClient {
    
    @PostMapping("/v1/transacciones")
    ResponseEntity<TransaccionSimpleDTO> ejecutarTransaccion(@RequestBody TransaccionSimpleDTO transaccion);
} 