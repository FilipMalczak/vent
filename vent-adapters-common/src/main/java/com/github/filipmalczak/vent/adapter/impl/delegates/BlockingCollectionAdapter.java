package com.github.filipmalczak.vent.adapter.impl.delegates;

import com.github.filipmalczak.vent.api.blocking.BlockingVentCollection;
import com.github.filipmalczak.vent.api.model.EventConfirmation;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import lombok.Value;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Value
public class BlockingCollectionAdapter implements BlockingVentCollection<BlockingQueryBuilderAdapter, BlockingQueryAdapter> {
    private ReactiveVentCollection<?, ?> ventCollection;

    @Override
    public String getVentCollectionName() {
        return ventCollection.getVentCollectionName();
    }

    @Override
    public Success drop() {
        return ventCollection.drop().block();
    }

    @Override
    public VentId create(Map initialState) {
        return ventCollection.create(initialState).block();
    }

    @Override
    public EventConfirmation putValue(VentId id, String path, Object value) {
        return ventCollection.putValue(id, path, value).block();
    }

    @Override
    public EventConfirmation deleteValue(VentId id, String path) {
        return ventCollection.deleteValue(id, path).block();
    }

    @Override
    public ObjectSnapshot get(VentId id, Supplier queryAt) {
        return (ObjectSnapshot) ventCollection.get(id, queryAt).block();
    }

    @Override
    public Stream<ObjectSnapshot> identifyAll(Supplier queryAt) {
        return ventCollection.identifyAll(queryAt).toStream();
    }

    @Override
    public EventConfirmation update(VentId id, Map newState) {
        return ventCollection.update(id, newState).block();
    }

    @Override
    public EventConfirmation delete(VentId id) {
        return ventCollection.delete(id).block();
    }

    @Override
    public BlockingQueryBuilderAdapter queryBuilder() {
        return new BlockingQueryBuilderAdapter(ventCollection.queryBuilder());
    }

    @Override
    public TemporalService getTemporalService() {
        return ventCollection.getTemporalService();
    }
}
