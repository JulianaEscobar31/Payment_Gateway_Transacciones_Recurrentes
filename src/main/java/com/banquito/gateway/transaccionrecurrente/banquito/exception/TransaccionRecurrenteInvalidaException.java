package com.banquito.gateway.transaccionrecurrente.banquito.exception;

public class TransaccionRecurrenteInvalidaException extends RuntimeException {
    
    private final String mensaje;

    public TransaccionRecurrenteInvalidaException(String mensaje) {
        super();
        this.mensaje = mensaje;
    }

    @Override
    public String getMessage() {
        return "Error en la transacción recurrente: " + mensaje;
    }
} 