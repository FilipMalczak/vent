package com.github.filipmalczak.vent.api.reactive;

import com.github.filipmalczak.vent.api.VentId;
import com.github.filipmalczak.vent.embedded.model.ObjectSnapshot;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

public interface ReactiveVentCollection {
    Mono<VentId> create(Map initialState);

    default Mono<VentId> create(){
        return create(Collections.emptyMap());
    }

    Mono<ObjectSnapshot> get(VentId id, LocalDateTime queryAt);
}
