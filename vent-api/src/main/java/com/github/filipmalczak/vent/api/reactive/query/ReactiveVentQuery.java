package com.github.filipmalczak.vent.api.reactive.query;

import com.github.filipmalczak.vent.api.general.query.VentQuery;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.traits.paradigm.Reactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.function.Supplier;


public interface ReactiveVentQuery extends VentQuery<Flux<ObjectSnapshot>, Mono<Long>, Mono<Boolean>>, Reactive {
    Flux<ObjectSnapshot> find(Supplier<LocalDateTime> queryAt);

    @Override
    default Mono<Long> count(Supplier<LocalDateTime> queryAt) {
        return find(queryAt).count();
    }

    @Override
    default Mono<Boolean> exists(Supplier<LocalDateTime> queryAt) {
        return find(queryAt).hasElements();
    }
}
