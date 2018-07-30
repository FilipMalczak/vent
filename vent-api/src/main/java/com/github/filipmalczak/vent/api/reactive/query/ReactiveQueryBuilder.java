package com.github.filipmalczak.vent.api.reactive.query;

import com.github.filipmalczak.vent.api.general.query.QueryBuilder;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.traits.paradigm.Reactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveQueryBuilder<
        This extends ReactiveQueryBuilder<This, QueryImpl>,
        QueryImpl extends ReactiveVentQuery
    > extends QueryBuilder<Flux<ObjectSnapshot>, Mono<Long>, Mono<Boolean>, This, QueryImpl>, Reactive{
}
