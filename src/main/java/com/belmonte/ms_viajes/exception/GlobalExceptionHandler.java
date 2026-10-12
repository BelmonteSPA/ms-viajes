package com.belmonte.ms_viajes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //400 - errores de validacion del RequestDTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex
                .getBindingResult()
                .getFieldErrors()
                .forEach(error -> errores.put(
                        error.getField(),
                        error.getDefaultMessage()
                ));
        return ResponseEntity.badRequest().body(errores);
    }

    //404 - viaje no encontrado
    @ExceptionHandler(ViajeNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleNoEncontrado(ViajeNoEncontradoException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mensaje(ex.getMessage()));
    }

    //409 - la accion no corresponde al estado actual del viaje
    @ExceptionHandler(EstadoViajeInvalidoException.class)
    public ResponseEntity<Map<String, String>> handleEstadoInvalido(EstadoViajeInvalidoException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(mensaje(ex.getMessage()));
    }

    //400 - pasajero o conductor invalido
    @ExceptionHandler(ReferenciaInvalidaException.class)
    public ResponseEntity<Map<String, String>> handleReferenciaInvalida(ReferenciaInvalidaException ex) {
        return ResponseEntity.badRequest().body(mensaje(ex.getMessage()));
    }

    //503 - otro microservicio caido
    @ExceptionHandler(ServicioNoDisponibleException.class)
    public ResponseEntity<Map<String, String>> handleServicioNoDisponible(ServicioNoDisponibleException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(mensaje(ex.getMessage()));
    }

    //400 - cualquier otra regla de negocio
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(mensaje(ex.getMessage()));
    }

    private Map<String, String> mensaje(String texto) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", texto);
        return error;
    }
}
