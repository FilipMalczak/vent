package com.github.filipmalczak.vent.api.reactive;

import com.github.filipmalczak.vent.api.EventConfirmation;
import com.github.filipmalczak.vent.api.VentId;
import com.github.filipmalczak.vent.embedded.model.ObjectSnapshot;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

import static com.github.filipmalczak.vent.helper.Struct.map;

public interface ReactiveVentCollection {
    Mono<VentId> create(Map initialState);

    default Mono<VentId> create(){
        return create(map());
    }

    Mono<EventConfirmation> putValue(VentId id, String path, Object value);

    Mono<EventConfirmation> deleteValue(VentId id, String path);

    Mono<ObjectSnapshot> get(VentId id, LocalDateTime queryAt);
}
