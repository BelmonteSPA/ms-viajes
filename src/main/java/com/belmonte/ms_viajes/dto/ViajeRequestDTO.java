package com.belmonte.ms_viajes.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViajeRequestDTO {

    //id, conductor y estado no se incluyen: el viaje nace PENDIENTE y sin conductor

    @NotNull(message = "El pasajero es obligatorio")
    private Long idPasajero;

    @NotNull(message = "La fecha es obligatoria (formato yyyy-MM-dd)")
    @FutureOrPresent(message = "La fecha no puede estar en el pasado")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria (formato HH:mm)")
    private LocalTime hora;

    @NotBlank(message = "El origen es obligatorio")
    @Size(max = 200, message = "El origen no puede superar los 200 caracteres")
    private String origen;

    @NotBlank(message = "El destino es obligatorio")
    @Size(max = 200, message = "El destino no puede superar los 200 caracteres")
    private String destino;

    //opcional
    @Size(max = 20, message = "El numero de vuelo no puede superar los 20 caracteres")
    private String numeroVuelo;

    @NotNull(message = "La latitud de origen es obligatoria")
    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    private Double latitudOrigen;

    @NotNull(message = "La longitud de origen es obligatoria")
    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    private Double longitudOrigen;

    @NotNull(message = "La latitud de destino es obligatoria")
    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    private Double latitudDestino;

    @NotNull(message = "La longitud de destino es obligatoria")
    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    private Double longitudDestino;
}
