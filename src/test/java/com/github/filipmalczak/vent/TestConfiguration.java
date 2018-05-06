package com.github.filipmalczak.vent;

import com.github.filipmalczak.vent.testimpl.StackTracer;
import com.github.filipmalczak.vent.embedded.service.TemporalService;
import com.github.filipmalczak.vent.testimpl.TestingTemporalService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {
    @Bean
    public TemporalService temporalService(){
        return new TestingTemporalService();
    }

    @Bean
    public StackTracer stackTracer(){
        return StackTracer.builder().basePackageClass(VentApplication.class).build();
    }
}
