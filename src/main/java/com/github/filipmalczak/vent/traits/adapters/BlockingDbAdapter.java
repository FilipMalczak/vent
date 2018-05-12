package com.github.filipmalczak.vent.traits.adapters;

import com.github.filipmalczak.vent.api.blocking.BlockingVentCollection;
import com.github.filipmalczak.vent.api.blocking.BlockingVentDb;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import lombok.Value;

@Value
class BlockingDbAdapter implements BlockingVentDb {
    private final ReactiveVentDb ventDb;

    @Override
    public BlockingVentCollection getCollection(String collectionName) {
        return ventDb.getCollection(collectionName).asBlocking();
    }

    @Override
    public ReactiveVentDb asReactive() {
        return ventDb;
    }
}
