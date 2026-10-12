package com.belmonte.ms_viajes.repository;

import com.belmonte.ms_viajes.model.EstadoViaje;
import com.belmonte.ms_viajes.model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, Long> {

    //solicitudes por atender, las mas proximas primero
    List<Viaje> findByEstadoOrderByFechaAscHoraAsc(EstadoViaje estado);

    List<Viaje> findByIdPasajeroOrderByFechaDescHoraDesc(Long idPasajero);

    List<Viaje> findByIdConductorOrderByFechaAscHoraAsc(Long idConductor);

    List<Viaje> findByIdConductorAndFechaOrderByHoraAsc(Long idConductor, LocalDate fecha);

    List<Viaje> findByFechaOrderByHoraAsc(LocalDate fecha);
}
