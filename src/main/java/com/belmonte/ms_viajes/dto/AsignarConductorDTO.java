package com.belmonte.ms_viajes.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarConductorDTO {

    @NotNull(message = "El conductor es obligatorio")
    private Long idConductor;
}
