package com.belmonte.ms_viajes.controller;

import com.belmonte.ms_viajes.dto.AsignarConductorDTO;
import com.belmonte.ms_viajes.dto.ViajeRequestDTO;
import com.belmonte.ms_viajes.dto.ViajeResponseDTO;
import com.belmonte.ms_viajes.service.ViajeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/viajes")
public class ViajeController {
    private final ViajeService viajeService;

    @Autowired
    public ViajeController(ViajeService viajeService) {
        this.viajeService = viajeService;
    }

    //***SOLICITANTE***
    //POST /viajes  (400 si hay errores o el pasajero no es valido, 503 si ms-pasajeros no responde)
    @PostMapping
    public ResponseEntity<ViajeResponseDTO> postViaje(@Valid @RequestBody ViajeRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(viajeService.save(dto));
    }

    //GET /viajes/pasajero/{idPasajero}
    @GetMapping("pasajero/{idPasajero}")
    public ResponseEntity<List<ViajeResponseDTO>> getPorPasajero(@PathVariable Long idPasajero) {
        return ResponseEntity.ok(viajeService.mostrarPorPasajero(idPasajero));
    }

    //DELETE /viajes/id  (solo si esta PENDIENTE)
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteViaje(@PathVariable Long id) {
        viajeService.borrar(id);
        return ResponseEntity.noContent().build();
    }

    //***ADMINISTRADOR***
    //GET /viajes
    @GetMapping
    public ResponseEntity<List<ViajeResponseDTO>> getTodos() {
        return ResponseEntity.ok(viajeService.mostrarViajes());
    }

    //GET /viajes/id  (404 si no existe)
    @GetMapping("{id}")
    public ResponseEntity<ViajeResponseDTO> getPorId(@PathVariable Long id) {
        return ResponseEntity.ok(viajeService.mostrarPorId(id));
    }

    //GET /viajes/pendientes
    @GetMapping("pendientes")
    public ResponseEntity<List<ViajeResponseDTO>> getPendientes() {
        return ResponseEntity.ok(viajeService.mostrarPendientes());
    }

    //GET /viajes/fecha/2026-10-12
    @GetMapping("fecha/{fecha}")
    public ResponseEntity<List<ViajeResponseDTO>> getPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(viajeService.mostrarPorFecha(fecha));
    }

    //PUT /viajes/id/asignar  body: {"idConductor": 1}
    //asigna o cambia el conductor (valida contra ms-conductores)
    @PutMapping("{id}/asignar")
    public ResponseEntity<ViajeResponseDTO> putAsignar(
            @PathVariable Long id,
            @Valid @RequestBody AsignarConductorDTO dto
    ) {
        return ResponseEntity.ok(viajeService.asignarConductor(id, dto));
    }

    //***CONDUCTOR***
    //GET /viajes/conductor/{idConductor}
    @GetMapping("conductor/{idConductor}")
    public ResponseEntity<List<ViajeResponseDTO>> getPorConductor(@PathVariable Long idConductor) {
        return ResponseEntity.ok(viajeService.mostrarPorConductor(idConductor));
    }

    //GET /viajes/conductor/{idConductor}/hoy
    @GetMapping("conductor/{idConductor}/hoy")
    public ResponseEntity<List<ViajeResponseDTO>> getDelDia(@PathVariable Long idConductor) {
        return ResponseEntity.ok(viajeService.mostrarDelDia(idConductor));
    }

    //PATCH /viajes/id/iniciar  (ASIGNADO -> EN_CURSO)
    @PatchMapping("{id}/iniciar")
    public ResponseEntity<ViajeResponseDTO> patchIniciar(@PathVariable Long id) {
        return ResponseEntity.ok(viajeService.iniciar(id));
    }

    //PATCH /viajes/id/finalizar  (EN_CURSO -> FINALIZADO)
    @PatchMapping("{id}/finalizar")
    public ResponseEntity<ViajeResponseDTO> patchFinalizar(@PathVariable Long id) {
        return ResponseEntity.ok(viajeService.finalizar(id));
    }
}
