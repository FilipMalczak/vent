package com.github.filipmalczak.vent;

import com.rits.cloning.Cloner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VentApplication {
	public static void main(String[] args) {
		SpringApplication.run(VentApplication.class, args);
	}
}
