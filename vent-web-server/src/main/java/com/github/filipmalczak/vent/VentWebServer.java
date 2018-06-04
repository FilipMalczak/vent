package com.github.filipmalczak.vent;

import com.github.filipmalczak.vent.web.integration.SnapshotMapping;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VentWebServer {
	public static void main(String[] args) {
		SpringApplication.run(VentWebServer.class, args);
	}
}
