package com.github.filipmalczak.vent.web.controller;

import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.web.model.TemporalStatusView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.github.filipmalczak.vent.web.paths.CommonPaths.*;

@RestController
public class TemporalController {
    @Autowired
    private TemporalService temporalService;

    @GetMapping(TEMPORAL)
    public Mono<TemporalStatusView> fullStatus(){
        return Mono.just(TemporalStatusView.builder().
                now(temporalService.now()).
                timezone(temporalService.getTimezone()).
                build()
        );
    }

    @GetMapping(TEMPORAL_NOW)
    public Mono<LocalDateTime> now(){
        return Mono.just(temporalService.now());
    }

    @GetMapping(TEMPORAL_TIMEZONE)
    public Mono<ZoneId> timezone(){
        return Mono.just(temporalService.getTimezone());
    }
}
