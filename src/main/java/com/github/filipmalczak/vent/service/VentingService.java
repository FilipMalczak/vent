package com.github.filipmalczak.vent.service;

import com.github.filipmalczak.vent.model.VentObject;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface VentingService {
    Mono<Map> applyVents(Mono<VentObject> object);
}
