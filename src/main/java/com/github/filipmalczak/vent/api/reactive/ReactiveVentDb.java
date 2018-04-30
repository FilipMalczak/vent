package com.github.filipmalczak.vent.api.reactive;

public interface ReactiveVentDb {
    ReactiveVentCollection getCollection(String collectionName);
}
