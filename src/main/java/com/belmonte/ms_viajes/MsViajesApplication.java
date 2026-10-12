package com.belmonte.ms_viajes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsViajesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsViajesApplication.class, args);
	}

}
