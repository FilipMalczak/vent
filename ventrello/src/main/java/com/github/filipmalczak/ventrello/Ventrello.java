package com.github.filipmalczak.ventrello;

import com.github.filipmalczak.vent.VentWebServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@Slf4j
public class Ventrello {
    public static void main(String[] args){
        ConfigurableApplicationContext context = run(new Class[]{Ventrello.class, VentWebServer.class}, args);
        log.info("Context initialized, "+Ventrello.class.getSimpleName()+" is running");
    }
}
