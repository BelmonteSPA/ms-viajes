package com.belmonte.ms_viajes.exception;

//se usa cuando la accion no corresponde al estado actual del viaje
public class EstadoViajeInvalidoException extends RuntimeException {

    public EstadoViajeInvalidoException(String mensaje) {
        super(mensaje);
    }
}
