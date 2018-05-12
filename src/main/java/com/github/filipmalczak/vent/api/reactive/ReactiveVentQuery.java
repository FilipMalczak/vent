package com.github.filipmalczak.vent.api.reactive;

import com.github.filipmalczak.vent.api.ObjectSnapshot;
import com.github.filipmalczak.vent.api.blocking.BlockingVentQuery;
import com.github.filipmalczak.vent.api.query.VentQuery;
import com.github.filipmalczak.vent.traits.Reactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Stream;

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
