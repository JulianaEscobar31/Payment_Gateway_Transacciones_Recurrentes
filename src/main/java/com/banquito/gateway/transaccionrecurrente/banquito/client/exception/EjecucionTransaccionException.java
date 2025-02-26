package com.banquito.gateway.transaccionrecurrente.banquito.client.exception;

public class EjecucionTransaccionException extends RuntimeException {
    
    private final String codigoTransaccion;
    
    public EjecucionTransaccionException(String codigoTransaccion, String mensaje) {
        super(mensaje);
        this.codigoTransaccion = codigoTransaccion;
    }
    
    public EjecucionTransaccionException(String codigoTransaccion, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigoTransaccion = codigoTransaccion;
    }
    
    public String getCodigoTransaccion() {
        return codigoTransaccion;
    }
    
    @Override
    public String getMessage() {
        return "Error al ejecutar la transacción recurrente [" + codigoTransaccion + "]: " + super.getMessage();
    }
} 