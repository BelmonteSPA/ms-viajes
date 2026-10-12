package com.belmonte.ms_viajes.service;

import com.belmonte.ms_viajes.client.ConductorClient;
import com.belmonte.ms_viajes.client.PasajeroClient;
import com.belmonte.ms_viajes.dto.AsignarConductorDTO;
import com.belmonte.ms_viajes.dto.ViajeRequestDTO;
import com.belmonte.ms_viajes.dto.ViajeResponseDTO;
import com.belmonte.ms_viajes.exception.EstadoViajeInvalidoException;
import com.belmonte.ms_viajes.exception.ReferenciaInvalidaException;
import com.belmonte.ms_viajes.exception.ServicioNoDisponibleException;
import com.belmonte.ms_viajes.exception.ViajeNoEncontradoException;
import com.belmonte.ms_viajes.model.EstadoViaje;
import com.belmonte.ms_viajes.model.Viaje;
import com.belmonte.ms_viajes.repository.ViajeRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ViajeService {
    private final ViajeRepository viajeRepository;
    private final PasajeroClient pasajeroClient;
    private final ConductorClient conductorClient;

    @Autowired
    public ViajeService(
            ViajeRepository viajeRepository,
            PasajeroClient pasajeroClient,
            ConductorClient conductorClient
    ) {
        this.viajeRepository = viajeRepository;
        this.pasajeroClient = pasajeroClient;
        this.conductorClient = conductorClient;
    }

    //Mapeo Entidad -> ResponseDTO
    private ViajeResponseDTO mapToDTO(Viaje viaje) {
        return new ViajeResponseDTO(
                viaje.getIdViaje(),
                viaje.getIdPasajero(),
                viaje.getIdConductor(),
                viaje.getFecha(),
                viaje.getHora(),
                viaje.getOrigen(),
                viaje.getDestino(),
                viaje.getNumeroVuelo(),
                viaje.getLatitudOrigen(),
                viaje.getLongitudOrigen(),
                viaje.getLatitudDestino(),
                viaje.getLongitudDestino(),
                viaje.getEstado()
        );
    }

    //Mapeo RequestDTO -> Entidad (nace PENDIENTE y sin conductor)
    private Viaje mapToEntity(ViajeRequestDTO dto) {
        return new Viaje(
                null,
                dto.getIdPasajero(),
                null,
                dto.getFecha(),
                dto.getHora(),
                dto.getOrigen(),
                dto.getDestino(),
                dto.getNumeroVuelo(),
                dto.getLatitudOrigen(),
                dto.getLongitudOrigen(),
                dto.getLatitudDestino(),
                dto.getLongitudDestino(),
                EstadoViaje.PENDIENTE
        );
    }

    private Viaje buscarOFallar(Long id) {
        return viajeRepository
                .findById(id)
                .orElseThrow(() -> new ViajeNoEncontradoException(id));
    }

    private List<ViajeResponseDTO> mapToList(List<Viaje> viajes) {
        return viajes
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    //***Validaciones contra otros microservicios (Feign)***
    private void validarPasajero(Long idPasajero) {
        try {
            if (!Boolean.TRUE.equals(pasajeroClient.validar(idPasajero))) {
                throw new ReferenciaInvalidaException("El pasajero con ID: " + idPasajero + " esta inactivo");
            }
        } catch (FeignException.NotFound e) {
            throw new ReferenciaInvalidaException("El pasajero con ID: " + idPasajero + " no existe");
        } catch (FeignException e) {
            log.error(">>> Error al consultar ms-pasajeros: {}", e.getMessage());
            throw new ServicioNoDisponibleException("ms-pasajeros");
        }
    }

    private void validarConductor(Long idConductor) {
        try {
            if (!Boolean.TRUE.equals(conductorClient.validar(idConductor))) {
                throw new ReferenciaInvalidaException(
                        "El conductor con ID: " + idConductor + " esta inactivo o no esta disponible");
            }
        } catch (FeignException.NotFound e) {
            throw new ReferenciaInvalidaException("El conductor con ID: " + idConductor + " no existe");
        } catch (FeignException e) {
            log.error(">>> Error al consultar ms-conductores: {}", e.getMessage());
            throw new ServicioNoDisponibleException("ms-conductores");
        }
    }

    //***SOLICITANTE***
    //POST /viajes  (crea la solicitud en estado PENDIENTE)
    public ViajeResponseDTO save(ViajeRequestDTO dto) {
        validarPasajero(dto.getIdPasajero());
        Viaje guardado = viajeRepository.save(mapToEntity(dto));
        log.info(">>> Solicitud de viaje {} creada (PENDIENTE)", guardado.getIdViaje());
        return mapToDTO(guardado);
    }

    //GET /viajes/pasajero/{idPasajero}
    public List<ViajeResponseDTO> mostrarPorPasajero(Long idPasajero) {
        return mapToList(viajeRepository.findByIdPasajeroOrderByFechaDescHoraDesc(idPasajero));
    }

    //DELETE /viajes/id  (solo se puede cancelar una solicitud PENDIENTE)
    public void borrar(Long id) {
        Viaje viaje = buscarOFallar(id);
        if (viaje.getEstado() != EstadoViaje.PENDIENTE) {
            throw new EstadoViajeInvalidoException(
                    "Solo se puede cancelar un viaje PENDIENTE (estado actual: " + viaje.getEstado() + ")");
        }
        viajeRepository.deleteById(id);
    }

    //***ADMINISTRADOR***
    //GET /viajes
    public List<ViajeResponseDTO> mostrarViajes() {
        return mapToList(viajeRepository.findAll());
    }

    //GET /viajes/id
    public ViajeResponseDTO mostrarPorId(Long id) {
        return mapToDTO(buscarOFallar(id));
    }

    //GET /viajes/pendientes
    public List<ViajeResponseDTO> mostrarPendientes() {
        return mapToList(viajeRepository.findByEstadoOrderByFechaAscHoraAsc(EstadoViaje.PENDIENTE));
    }

    //GET /viajes/fecha/{fecha}
    public List<ViajeResponseDTO> mostrarPorFecha(LocalDate fecha) {
        return mapToList(viajeRepository.findByFechaOrderByHoraAsc(fecha));
    }

    //PUT /viajes/id/asignar  (PENDIENTE -> ASIGNADO; tambien permite cambiar el conductor si ya esta ASIGNADO)
    public ViajeResponseDTO asignarConductor(Long id, AsignarConductorDTO dto) {
        Viaje viaje = buscarOFallar(id);
        if (viaje.getEstado() != EstadoViaje.PENDIENTE && viaje.getEstado() != EstadoViaje.ASIGNADO) {
            throw new EstadoViajeInvalidoException(
                    "No se puede asignar un conductor a un viaje " + viaje.getEstado());
        }
        validarConductor(dto.getIdConductor());

        viaje.setIdConductor(dto.getIdConductor());
        viaje.setEstado(EstadoViaje.ASIGNADO);
        log.info(">>> Viaje {} asignado al conductor {}", id, dto.getIdConductor());
        return mapToDTO(viajeRepository.save(viaje));
    }

    //***CONDUCTOR***
    //GET /viajes/conductor/{idConductor}
    public List<ViajeResponseDTO> mostrarPorConductor(Long idConductor) {
        return mapToList(viajeRepository.findByIdConductorOrderByFechaAscHoraAsc(idConductor));
    }

    //GET /viajes/conductor/{idConductor}/hoy
    public List<ViajeResponseDTO> mostrarDelDia(Long idConductor) {
        return mapToList(viajeRepository.findByIdConductorAndFechaOrderByHoraAsc(idConductor, LocalDate.now()));
    }

    //PATCH /viajes/id/iniciar  (ASIGNADO -> EN_CURSO)
    public ViajeResponseDTO iniciar(Long id) {
        Viaje viaje = buscarOFallar(id);
        if (viaje.getEstado() != EstadoViaje.ASIGNADO) {
            throw new EstadoViajeInvalidoException(
                    "Solo se puede iniciar un viaje ASIGNADO (estado actual: " + viaje.getEstado() + ")");
        }
        viaje.setEstado(EstadoViaje.EN_CURSO);
        return mapToDTO(viajeRepository.save(viaje));
    }

    //PATCH /viajes/id/finalizar  (EN_CURSO -> FINALIZADO)
    public ViajeResponseDTO finalizar(Long id) {
        Viaje viaje = buscarOFallar(id);
        if (viaje.getEstado() != EstadoViaje.EN_CURSO) {
            throw new EstadoViajeInvalidoException(
                    "Solo se puede finalizar un viaje EN_CURSO (estado actual: " + viaje.getEstado() + ")");
        }
        viaje.setEstado(EstadoViaje.FINALIZADO);
        return mapToDTO(viajeRepository.save(viaje));
    }
}
