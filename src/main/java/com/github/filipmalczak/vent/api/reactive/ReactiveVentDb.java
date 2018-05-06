package com.github.filipmalczak.vent.api.reactive;

import com.github.filipmalczak.vent.api.EventConfirmation;
import com.github.filipmalczak.vent.api.ObjectSnapshot;
import com.github.filipmalczak.vent.api.VentId;
import com.github.filipmalczak.vent.api.blocking.BlockingVentCollection;
import com.github.filipmalczak.vent.api.blocking.BlockingVentDb;

import java.time.LocalDateTime;
import java.util.Map;

public interface ReactiveVentDb {
    ReactiveVentCollection getCollection(String collectionName);

    //todo: consider adding version with Duration
    default BlockingVentDb asBlocking(){
        return collectionName -> new BlockingVentCollection() {
            private ReactiveVentCollection delegate = ReactiveVentDb.this.getCollection(collectionName);

            @Override
            public VentId create(Map initialState) {
                return delegate.create(initialState).block();
            }

            @Override
            public EventConfirmation putValue(VentId id, String path, Object value) {
                return delegate.putValue(id, path, value).block();
            }

            @Override
            public EventConfirmation deleteValue(VentId id, String path) {
                return delegate.deleteValue(id, path).block();
            }

            @Override
            public ObjectSnapshot get(VentId id, LocalDateTime queryAt) {
                return delegate.get(id, queryAt).block();
            }

            @Override
            public EventConfirmation update(VentId id, Map newState) {
                return delegate.update(id, newState).block();
            }
        };
    }
}
