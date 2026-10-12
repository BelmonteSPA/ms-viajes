package com.belmonte.ms_viajes.exception;

public class ViajeNoEncontradoException extends RuntimeException {

    public ViajeNoEncontradoException(Long id) {
        super("El viaje con ID: " + id + " no existe");
    }
}
