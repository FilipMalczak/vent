package com.github.filipmalczak.vent.api.reactive;

import com.github.filipmalczak.vent.api.blocking.BlockingVentCollection;
import com.github.filipmalczak.vent.api.general.VentCollection;
import com.github.filipmalczak.vent.api.model.EventConfirmation;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveQueryBuilder;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveVentQuery;
import com.github.filipmalczak.vent.traits.Reactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.github.filipmalczak.vent.helper.Struct.map;
import static com.github.filipmalczak.vent.traits.adapters.Adapters.adapt;


public interface ReactiveVentCollection extends VentCollection<
    Mono<Success>, Mono<VentId>, Mono<EventConfirmation>, Mono<ObjectSnapshot>,
    Flux<VentId>, Flux<ObjectSnapshot>, ReactiveQueryBuilder<?, ? extends ReactiveVentQuery>
    >, Reactive<BlockingVentCollection> {
    default Mono<VentId> create(){
        return create(map());
    }

    default Flux<ObjectSnapshot> getAll(LocalDateTime queryAt){
        return identifyAll(queryAt).flatMap(id -> get(id, queryAt));
    }

    @Override
    default BlockingVentCollection asBlocking() {
        return adapt(this);
    }
}
