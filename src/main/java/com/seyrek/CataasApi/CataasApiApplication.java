package com.seyrek.CataasApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CataasApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CataasApiApplication.class, args);
	}

}
