package com.github.filipmalczak.vent.api.reactive;

import com.github.filipmalczak.vent.api.general.VentDb;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.traits.paradigm.Reactive;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ReactiveVentDb extends VentDb<ReactiveVentCollection, Flux<String>, Mono<Success>, Mono<Boolean>>, Reactive {

    default Mono<Boolean> isManaged(@NonNull String collectionName){
        return getManagedCollections().any(collectionName::equals);
    }
}
