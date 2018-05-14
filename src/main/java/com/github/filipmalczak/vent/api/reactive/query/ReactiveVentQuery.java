package com.github.filipmalczak.vent.api.reactive.query;

import com.github.filipmalczak.vent.api.blocking.query.BlockingVentQuery;
import com.github.filipmalczak.vent.api.general.query.VentQuery;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.traits.Reactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.github.filipmalczak.vent.traits.adapters.Adapters.adapt;


public interface ReactiveVentQuery extends Reactive<BlockingVentQuery>, VentQuery<Flux<ObjectSnapshot>, Mono<Long>, Mono<Boolean>> {
    Flux<ObjectSnapshot> find(LocalDateTime queryAt);

    @Override
    default Mono<Long> count(LocalDateTime queryAt) {
        return find(queryAt).count();
    }

    @Override
    default Mono<Boolean> exists(LocalDateTime queryAt) {
        return find(queryAt).hasElements();
    }

    @Override
    default BlockingVentQuery asBlocking() {
        return adapt(this);
    }
}
