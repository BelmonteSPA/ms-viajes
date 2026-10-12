package com.belmonte.ms_viajes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//Con Eureka activo, "clients.conductores.url" se deja vacio y se resuelve por el nombre ms-conductores
@FeignClient(name = "ms-conductores", url = "${clients.conductores.url:}")
public interface ConductorClient {

    //true si el conductor existe, esta activo y disponible (404 si no existe)
    @GetMapping("/api/v1/conductores/{id}/validar")
    Boolean validar(@PathVariable("id") Long id);
}
