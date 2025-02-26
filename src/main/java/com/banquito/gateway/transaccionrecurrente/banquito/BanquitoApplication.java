package com.banquito.gateway.transaccionrecurrente.banquito;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BanquitoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BanquitoApplication.class, args);
	}

}
