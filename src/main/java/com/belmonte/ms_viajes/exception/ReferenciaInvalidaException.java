package com.belmonte.ms_viajes.exception;

//pasajero o conductor inexistente, inactivo o no disponible
public class ReferenciaInvalidaException extends RuntimeException {

    public ReferenciaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
