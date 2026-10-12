package com.belmonte.ms_viajes.config;

import com.belmonte.ms_viajes.model.EstadoViaje;
import com.belmonte.ms_viajes.model.Viaje;
import com.belmonte.ms_viajes.repository.ViajeRepository;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Carga datos 100% sintéticos (el anexo prohíbe usar datos reales).
 * Solo corre en el perfil "dev" y únicamente si la tabla está vacía.
 * Los viajes referencian ids de pasajeros (1..100) y conductores (1..20) de los otros
 * microservicios; no se validan aquí porque cada servicio tiene su propia base de datos.
 */
@Slf4j
@Configuration
@Profile("dev")
public class DataInitializer {

    private static final int TOTAL_VIAJES = 50;
    private static final int TOTAL_PASAJEROS = 100;
    private static final int TOTAL_CONDUCTORES = 20;

    //lugares de prueba en Santiago con coordenadas aproximadas (indice 0 = aeropuerto)
    private record Lugar(String direccion, double latitud, double longitud) {}

    private static final List<Lugar> LUGARES = List.of(
            new Lugar("Aeropuerto Internacional SCL, Pudahuel", -33.3930, -70.7858),
            new Lugar("Oficina Corporativa, Av. Apoquindo 4500, Las Condes", -33.4089, -70.5676),
            new Lugar("Edificio Empresarial, Av. Providencia 1200, Providencia", -33.4263, -70.6118),
            new Lugar("Hotel Centro, Plaza de Armas, Santiago", -33.4372, -70.6506),
            new Lugar("Centro de Negocios, Av. Vitacura 3000, Vitacura", -33.3910, -70.5750),
            new Lugar("Terminal Estacion Central, Santiago", -33.4524, -70.6785),
            new Lugar("Planta Industrial, Av. Pajaritos 3500, Maipu", -33.5100, -70.7580),
            new Lugar("Oficinas Nunoa, Av. Irarrazaval 2800, Nunoa", -33.4540, -70.5990)
    );

    @Bean
    CommandLineRunner cargarViajes(ViajeRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            Faker faker = new Faker(Locale.forLanguageTag("es"));
            List<Viaje> viajes = new ArrayList<>();

            for (int i = 0; i < TOTAL_VIAJES; i++) {
                //origen y destino distintos
                Lugar origen = LUGARES.get(faker.number().numberBetween(0, LUGARES.size()));
                Lugar destino;
                do {
                    destino = LUGARES.get(faker.number().numberBetween(0, LUGARES.size()));
                } while (destino == origen);

                //numero de vuelo solo si el traslado sale o llega al aeropuerto
                String vuelo = null;
                if (origen == LUGARES.get(0) || destino == LUGARES.get(0)) {
                    vuelo = faker.regexify("[A-Z]{2}[0-9]{3}");
                }

                //estado coherente con la fecha: pasado = FINALIZADO, hoy = ASIGNADO/EN_CURSO, futuro = PENDIENTE/ASIGNADO
                int dias = faker.number().numberBetween(-3, 8);
                boolean mitad = faker.bool().bool();
                EstadoViaje estado;
                if (dias < 0) {
                    estado = EstadoViaje.FINALIZADO;
                } else if (dias == 0) {
                    estado = mitad ? EstadoViaje.EN_CURSO : EstadoViaje.ASIGNADO;
                } else {
                    estado = mitad ? EstadoViaje.ASIGNADO : EstadoViaje.PENDIENTE;
                }

                //solo los viajes PENDIENTE no tienen conductor
                Long idConductor = estado == EstadoViaje.PENDIENTE
                        ? null
                        : (long) faker.number().numberBetween(1, TOTAL_CONDUCTORES + 1);

                viajes.add(new Viaje(
                        null,
                        (long) faker.number().numberBetween(1, TOTAL_PASAJEROS + 1),
                        idConductor,
                        LocalDate.now().plusDays(dias),
                        LocalTime.of(faker.number().numberBetween(5, 23), faker.number().numberBetween(0, 4) * 15),
                        origen.direccion(),
                        destino.direccion(),
                        vuelo,
                        origen.latitud(),
                        origen.longitud(),
                        destino.latitud(),
                        destino.longitud(),
                        estado
                ));
            }
            repository.saveAll(viajes);
            log.info(">>> {} viajes sintéticos cargados", repository.count());
        };
    }
}
