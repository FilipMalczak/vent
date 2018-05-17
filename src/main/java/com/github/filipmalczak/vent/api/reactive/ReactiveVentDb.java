package com.github.filipmalczak.vent.api.reactive;

import com.github.filipmalczak.vent.api.blocking.BlockingVentDb;
import com.github.filipmalczak.vent.api.general.VentDb;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.traits.Reactive;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.github.filipmalczak.vent.traits.adapters.Adapters.adapt;


public interface ReactiveVentDb extends VentDb<ReactiveVentCollection, Flux<String>, Mono<Success>, Mono<Boolean>>, Reactive<BlockingVentDb> {

    //todo: consider adding version with Duration
    default BlockingVentDb asBlocking(){
        return adapt(this);
    }

    default Mono<Boolean> isManaged(@NonNull String collectionName){
        return getManagedCollections().any(collectionName::equals);
    }
}
