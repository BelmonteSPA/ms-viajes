CREATE TABLE viajes (
    id_viaje          BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_pasajero       BIGINT       NOT NULL,
    id_conductor      BIGINT       NULL,
    fecha             DATE         NOT NULL,
    hora              TIME         NOT NULL,
    origen            VARCHAR(200) NOT NULL,
    destino           VARCHAR(200) NOT NULL,
    numero_vuelo      VARCHAR(20)  NULL,
    latitud_origen    DOUBLE       NOT NULL,
    longitud_origen   DOUBLE       NOT NULL,
    latitud_destino   DOUBLE       NOT NULL,
    longitud_destino  DOUBLE       NOT NULL,
    estado            VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE',
    INDEX idx_viajes_estado (estado),
    INDEX idx_viajes_pasajero (id_pasajero),
    INDEX idx_viajes_conductor_fecha (id_conductor, fecha)
);
