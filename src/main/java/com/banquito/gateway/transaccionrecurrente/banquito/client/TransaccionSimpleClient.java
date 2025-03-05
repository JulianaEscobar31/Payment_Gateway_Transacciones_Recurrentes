package com.banquito.gateway.transaccionrecurrente.banquito.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.banquito.gateway.transaccionrecurrente.banquito.client.dto.TransaccionSimpleDTO;

@FeignClient(name = "transaccion-simple", url ="http://transaccionsimple-alb-705840120.us-east-2.elb.amazonaws.com/swagger-ui/index.html")
public interface TransaccionSimpleClient {
     
    @PostMapping("/api/v1/transacciones/recurrentes")
    ResponseEntity<TransaccionSimpleDTO> ejecutarTransaccion(@RequestBody TransaccionSimpleDTO transaccion);
} 