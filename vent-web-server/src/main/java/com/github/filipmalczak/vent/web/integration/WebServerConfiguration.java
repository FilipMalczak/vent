package com.github.filipmalczak.vent.web.integration;

import com.github.filipmalczak.vent.embedded.EmbeddedVentCodecs;
import ma.glasnost.orika.MapperFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
public class WebServerConfiguration {
    @Bean
    public MapperFacade mapperFacade(){
        return new MapperFacadeImpl();
    }
}
