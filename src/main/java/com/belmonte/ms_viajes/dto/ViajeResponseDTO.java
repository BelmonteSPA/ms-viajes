package com.belmonte.ms_viajes.dto;

import com.belmonte.ms_viajes.model.EstadoViaje;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViajeResponseDTO {

    private Long id;
    private Long idPasajero;
    private Long idConductor;
    private LocalDate fecha;
    private LocalTime hora;
    private String origen;
    private String destino;
    private String numeroVuelo;
    private double latitudOrigen;
    private double longitudOrigen;
    private double latitudDestino;
    private double longitudDestino;
    private EstadoViaje estado;
}
