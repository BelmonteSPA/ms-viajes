package com.belmonte.ms_viajes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//Con Eureka activo, "clients.pasajeros.url" se deja vacio y se resuelve por el nombre ms-pasajeros
@FeignClient(name = "ms-pasajeros", url = "${clients.pasajeros.url:}")
public interface PasajeroClient {

    //true si el pasajero existe y esta activo (404 si no existe)
    @GetMapping("/api/v1/pasajeros/{id}/validar")
    Boolean validar(@PathVariable("id") Long id);
}
