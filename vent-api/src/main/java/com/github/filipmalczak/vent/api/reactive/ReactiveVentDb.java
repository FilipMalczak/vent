package com.github.filipmalczak.vent.api.reactive;

import com.github.filipmalczak.vent.api.general.VentDb;
import com.github.filipmalczak.vent.api.model.EventConfirmation;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveQueryBuilder;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveVentQuery;
import com.github.filipmalczak.vent.traits.paradigm.Reactive;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ReactiveVentDb<
            CollectionImpl extends ReactiveVentCollection<QueryBuilderImpl, QueryImpl>,
            QueryBuilderImpl extends ReactiveQueryBuilder<QueryBuilderImpl, QueryImpl>,
            QueryImpl extends ReactiveVentQuery
        > extends VentDb<
            Mono<Boolean>, Mono<Success>, Mono<VentId>, Mono<EventConfirmation>, Mono<ObjectSnapshot>,
            Flux<String>, Flux<VentId>, Flux<ObjectSnapshot>,
            Flux<ObjectSnapshot>, Mono<Long>, Mono<Boolean>,
            CollectionImpl,
            QueryBuilderImpl, QueryImpl
        >, Reactive {

    default Mono<Boolean> isManaged(@NonNull String collectionName){
        return getManagedCollections().any(collectionName::equals);
    }
}
