package com.github.filipmalczak.vent.api.reactive;

import com.github.filipmalczak.vent.api.general.VentCollection;
import com.github.filipmalczak.vent.api.general.VentCollectionReadOperations;
import com.github.filipmalczak.vent.api.model.EventConfirmation;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveQueryBuilder;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveVentQuery;
import com.github.filipmalczak.vent.traits.paradigm.Reactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;


public interface ReactiveVentCollection<
        QueryBuilderImpl extends ReactiveQueryBuilder<QueryBuilderImpl, QueryImpl>,
        QueryImpl extends ReactiveVentQuery
    > extends VentCollection<
            Mono<Success>, Mono<VentId>, Mono<EventConfirmation>, Mono<ObjectSnapshot>,
            Flux<VentId>, Flux<ObjectSnapshot>,
            Flux<ObjectSnapshot>, Mono<Long>, Mono<Boolean>,
            QueryBuilderImpl, QueryImpl
        >, Reactive {
    default Mono<VentId> create(){
        return create(new HashMap());
    }

    default Flux<ObjectSnapshot> getAll(LocalDateTime queryAt){
        return identifyAll(queryAt).flatMap(id -> get(id, queryAt));
    }
}
