package com.belmonte.ms_viajes.exception;

//otro microservicio no responde
public class ServicioNoDisponibleException extends RuntimeException {

    public ServicioNoDisponibleException(String servicio) {
        super("El servicio " + servicio + " no esta disponible en este momento");
    }
}
