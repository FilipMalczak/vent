package com.github.filipmalczak.vent.api.reactive;

import com.github.filipmalczak.vent.api.EventConfirmation;
import com.github.filipmalczak.vent.api.ObjectSnapshot;
import com.github.filipmalczak.vent.api.Success;
import com.github.filipmalczak.vent.api.VentId;
import com.github.filipmalczak.vent.api.blocking.BlockingVentCollection;
import com.github.filipmalczak.vent.api.traits.Reactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

import static com.github.filipmalczak.vent.helper.Struct.map;

public interface ReactiveVentCollection extends Reactive<BlockingVentCollection> {
    Mono<Success> drop();

    Mono<VentId> create(Map initialState);

    default Mono<VentId> create(){
        return create(map());
    }

    Mono<EventConfirmation> putValue(VentId id, String path, Object value);

    Mono<EventConfirmation> deleteValue(VentId id, String path);

    Mono<ObjectSnapshot> get(VentId id, LocalDateTime queryAt);

    Flux<VentId> identifyAll(LocalDateTime queryAt);

    default Flux<ObjectSnapshot> getAll(LocalDateTime queryAt){
        return identifyAll(queryAt).flatMap(id -> get(id, queryAt));
    }

    Mono<EventConfirmation> update(VentId id, Map newState);
}
