package com.banquito.gateway.transaccionrecurrente.banquito.exception;

public class TransaccionRecurrenteNotFoundException extends RuntimeException {
    
    private final String codigo;

    public TransaccionRecurrenteNotFoundException(String codigo) {
        super();
        this.codigo = codigo;
    }

    @Override
    public String getMessage() {
        return "No se encontró la transacción recurrente con código: " + codigo;
    }
} 