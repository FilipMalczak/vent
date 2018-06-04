package com.github.filipmalczak.vent.adapter.impl.delegates;

import com.github.filipmalczak.vent.api.blocking.BlockingVentCollection;
import com.github.filipmalczak.vent.api.blocking.BlockingVentDb;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import lombok.Value;

import java.util.stream.Stream;

@Value
public class BlockingDbAdapter implements BlockingVentDb {
    private final ReactiveVentDb ventDb;

    @Override
    public BlockingVentCollection getCollection(String collectionName) {
        return new BlockingCollectionAdapter(ventDb.getCollection(collectionName));
    }

    @Override
    public Success optimizePages(SuggestionStrength strength, OptimizationType type) {
        return ventDb.optimizePages(strength, type).block();
    }

    @Override
    public Stream<String> getManagedCollections() {
        return ventDb.getManagedCollections().toStream();
    }

    @Override
    public Boolean isManaged(String collectionName) {
        return ventDb.isManaged(collectionName).block();
    }

    @Override
    public Success manage(String collectionName) {
        return ventDb.manage(collectionName).block();
    }

    @Override
    public TemporalService getTemporalService() {
        return ventDb.getTemporalService();
    }
}
