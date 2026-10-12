package com.belmonte.ms_viajes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "viajes")
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_viaje")
    private Long idViaje;

    //id del pasajero en ms-pasajeros (no hay FK porque son bases de datos separadas)
    @Column(name = "id_pasajero", nullable = false)
    private Long idPasajero;

    //id del conductor en ms-conductores; null mientras el viaje esta PENDIENTE
    @Column(name = "id_conductor")
    private Long idConductor;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(nullable = false, length = 200)
    private String origen;

    @Column(nullable = false, length = 200)
    private String destino;

    //solo cuando corresponde (traslados desde/hacia el aeropuerto)
    @Column(name = "numero_vuelo", length = 20)
    private String numeroVuelo;

    @Column(name = "latitud_origen", nullable = false)
    private double latitudOrigen;

    @Column(name = "longitud_origen", nullable = false)
    private double longitudOrigen;

    @Column(name = "latitud_destino", nullable = false)
    private double latitudDestino;

    @Column(name = "longitud_destino", nullable = false)
    private double longitudDestino;

    //se guarda como texto (VARCHAR) para que Flyway y Hibernate coincidan en MySQL
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private EstadoViaje estado = EstadoViaje.PENDIENTE;
}
