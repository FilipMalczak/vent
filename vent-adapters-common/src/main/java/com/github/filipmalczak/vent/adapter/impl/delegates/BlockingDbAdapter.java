package com.github.filipmalczak.vent.adapter.impl.delegates;

import com.github.filipmalczak.vent.api.blocking.BlockingVentCollection;
import com.github.filipmalczak.vent.api.blocking.BlockingVentDb;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import lombok.Value;

@Value
public class BlockingDbAdapter implements BlockingVentDb {
    private final ReactiveVentDb ventDb;

    @Override
    public BlockingVentCollection getCollection(String collectionName) {
        return new BlockingCollectionAdapter(ventDb.getCollection(collectionName));
    }
}
