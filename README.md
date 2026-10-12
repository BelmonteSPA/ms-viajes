# Belmonte SpA - Microservicio de Viajes (ms-viajes)

Gestiona las solicitudes de traslado corporativo y su ciclo de vida. Para crear una solicitud valida al pasajero
en `ms-pasajeros`, y para asignar un viaje valida al conductor en `ms-conductores` (ambos con OpenFeign).

## Flujo de estados

```
PENDIENTE --asignar--> ASIGNADO --iniciar--> EN_CURSO --finalizar--> FINALIZADO
```

- Solicitante crea la solicitud: queda `PENDIENTE`, sin conductor.
- Administrador asigna un conductor: pasa a `ASIGNADO` (puede cambiar el conductor mientras siga `ASIGNADO`).
- Conductor inicia el viaje: `EN_CURSO`, y al terminar lo finaliza: `FINALIZADO`.
- Una acción que no corresponde al estado actual responde 409.

## Tecnologías

- Java 25 / Spring Boot 4.0.6
- MySQL (Laragon) + Spring Data JPA, migraciones con Flyway
- Spring Cloud OpenFeign (comunicación con otros microservicios) y cliente Eureka (desactivado por ahora)
- Datafaker para datos sintéticos (el anexo prohíbe datos reales)

## Puerto

`http://localhost:8084`

## Endpoints (`/api/v1/viajes`)

| Método | Ruta | Quién | Descripción |
|---|---|---|---|
| POST | `/` | Solicitante | Crea una solicitud (valida pasajero en ms-pasajeros) |
| GET | `/pasajero/{idPasajero}` | Solicitante | Viajes de un pasajero |
| DELETE | `/{id}` | Solicitante | Cancela una solicitud (solo si está `PENDIENTE`) |
| GET | `/` | Administrador | Lista todos los viajes |
| GET | `/{id}` | Administrador | Detalle de un viaje (404 si no existe) |
| GET | `/pendientes` | Administrador | Solicitudes `PENDIENTE`, las más próximas primero |
| GET | `/fecha/{yyyy-MM-dd}` | Administrador | Viajes de una fecha |
| PUT | `/{id}/asignar` | Administrador | Asigna o cambia el conductor (valida en ms-conductores) |
| GET | `/conductor/{idConductor}` | Conductor | Viajes asignados a un conductor |
| GET | `/conductor/{idConductor}/hoy` | Conductor | Viajes del día de un conductor |
| PATCH | `/{id}/iniciar` | Conductor | `ASIGNADO` pasa a `EN_CURSO` |
| PATCH | `/{id}/finalizar` | Conductor | `EN_CURSO` pasa a `FINALIZADO` |

Ejemplo de `POST`:

```json
{
  "idPasajero": 1,
  "fecha": "2026-10-20",
  "hora": "08:30",
  "origen": "Aeropuerto Internacional SCL, Pudahuel",
  "destino": "Oficina Corporativa, Av. Apoquindo 4500, Las Condes",
  "numeroVuelo": "LA800",
  "latitudOrigen": -33.3930,
  "longitudOrigen": -70.7858,
  "latitudDestino": -33.4089,
  "longitudDestino": -70.5676
}
```

Ejemplo de `PUT /{id}/asignar`:

```json
{ "idConductor": 1 }
```

`numeroVuelo` es opcional. Las coordenadas permitirán abrir Google Maps o Waze desde la app del conductor.

## Comunicación con otros microservicios

| Servicio | URL por defecto | Variable | Endpoint usado |
|---|---|---|---|
| ms-pasajeros | `http://localhost:8082` | `MS_PASAJEROS_URL` | `GET /api/v1/pasajeros/{id}/validar` |
| ms-conductores | `http://localhost:8083` | `MS_CONDUCTORES_URL` | `GET /api/v1/conductores/{id}/validar` |

Para crear o asignar viajes, esos dos servicios deben estar en ejecución. Si no responden, `ms-viajes` devuelve 503;
si el pasajero o conductor no existe o no está habilitado, devuelve 400.

## Base de datos (MySQL con Laragon)

1. Iniciar MySQL desde Laragon (puerto 3306).
2. La base `db_viajes` se crea sola al arrancar (`createDatabaseIfNotExist=true`).
3. Las tablas las crea Flyway al arrancar (`src/main/resources/db/migration`).
4. Credenciales por variables de entorno (por defecto, las de Laragon):

| Variable | Por defecto |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/db_viajes?createDatabaseIfNotExist=true&serverTimezone=America/Santiago` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | (vacía) |

## Ejecución local

El perfil `dev` viene activo en `application.yml`: si la tabla está vacía, carga 50 viajes sintéticos con estados
coherentes con su fecha (pasados `FINALIZADO`, de hoy `ASIGNADO` o `EN_CURSO`, futuros `PENDIENTE` o `ASIGNADO`).
Referencian pasajeros 1 a 100 y conductores 1 a 20.

```bash
./mvnw spring-boot:run
```

## Nota

Si falta el wrapper de Maven (`mvnw`, carpeta `.mvn`), generarlo con `mvn wrapper:wrapper` o crearlo desde el IDE.
