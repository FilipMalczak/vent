package com.github.filipmalczak.vent.embedded.model.events;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

import static reactor.core.publisher.Mono.just;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Update implements Event {
    private Map newValue;
    @Getter private LocalDateTime occuredOn;

    @Override
    public Mono<Map> apply(Mono<Map> mapMono) {
        return mapMono.then(just(newValue));
    }
}
