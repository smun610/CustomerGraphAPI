package com.example.customerGraphAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CustomerGraphApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerGraphApiApplication.class, args);
	}

}
