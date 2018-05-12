package com.github.filipmalczak.vent.api.query;

import com.github.filipmalczak.vent.api.ObjectSnapshot;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentQuery;
import com.github.filipmalczak.vent.traits.Reactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.github.filipmalczak.vent.traits.adapters.Adapters.adapt;

public interface ReactiveQueryBuilder
    <This extends ReactiveQueryBuilder<This, QueryImpl>, QueryImpl extends ReactiveVentQuery>
    extends
    QueryBuilder<This, QueryImpl, Flux<ObjectSnapshot>, Mono<Long>, Mono<Boolean>>, Reactive<BlockingQueryBuilder<?, ?>>{
    @Override
    default BlockingQueryBuilder<?, ?> asBlocking() {
        return adapt(this);
    }
}
