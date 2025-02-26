package com.banquito.gateway.transaccionrecurrente.banquito.exception;

public class EjecucionTransaccionException extends RuntimeException {
    
    private final String codigoTransaccion;
    private final String mensaje;

    public EjecucionTransaccionException(String codigoTransaccion, String mensaje) {
        super();
        this.codigoTransaccion = codigoTransaccion;
        this.mensaje = mensaje;
    }

    @Override
    public String getMessage() {
        return "Error al ejecutar la transacción " + this.codigoTransaccion + ": " + this.mensaje;
    }
} 