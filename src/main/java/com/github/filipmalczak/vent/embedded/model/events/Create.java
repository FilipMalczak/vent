package com.github.filipmalczak.vent.embedded.model.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

import static reactor.core.publisher.Mono.error;
import static reactor.core.publisher.Mono.just;

@AllArgsConstructor
public class Create implements Event{
    private Map initialState;
    @Getter private LocalDateTime occuredOn;
    @Override
    public Mono<Map> apply(Mono<Map> map) {
        return map.
            <Map>flatMap(x -> error(new RuntimeException("Create must happen first!"))).
            switchIfEmpty(just(initialState));
    }
}
