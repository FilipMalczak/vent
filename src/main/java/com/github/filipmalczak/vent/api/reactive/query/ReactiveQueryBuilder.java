package com.github.filipmalczak.vent.api.reactive.query;

import com.github.filipmalczak.vent.api.blocking.query.BlockingQueryBuilder;
import com.github.filipmalczak.vent.api.general.query.QueryBuilder;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
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
