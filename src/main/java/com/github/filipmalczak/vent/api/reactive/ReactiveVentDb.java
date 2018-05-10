package com.github.filipmalczak.vent.api.reactive;

import com.github.filipmalczak.vent.api.blocking.BlockingVentCollection;
import com.github.filipmalczak.vent.api.blocking.BlockingVentDb;
import com.github.filipmalczak.vent.api.traits.Reactive;

public interface ReactiveVentDb extends Reactive<BlockingVentDb> {
    ReactiveVentCollection getCollection(String collectionName);

    //todo: consider adding version with Duration
    default BlockingVentDb asBlocking(){
        return new BlockingVentDb() {
            @Override
            public BlockingVentCollection getCollection(String collectionName) {
                return asReactive().getCollection(collectionName).asBlocking();
            }

            @Override
            public ReactiveVentDb asReactive() {
                return ReactiveVentDb.this;
            }
        };
    }
}
