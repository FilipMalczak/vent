package com.github.filipmalczak.vent;

import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.testing.TestingTemporalService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

@Configuration
@TestPropertySource(properties = "local.server.port=8083")
public class TestServerConfig {
    @Bean
    public TemporalService temporalService(){
//        return reactiveVentDb.getTemporalService();
        return new TestingTemporalService();
    }
}
