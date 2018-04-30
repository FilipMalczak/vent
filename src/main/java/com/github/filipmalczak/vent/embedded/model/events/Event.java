package com.github.filipmalczak.vent.embedded.model.events;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

public interface Event extends Function<Mono<Map>, Mono<Map>> {
    LocalDateTime getOccuredOn();
}
