package com.github.filipmalczak.vent.api.general;

public interface VentDb<CollectionImpl extends VentCollection> {
    CollectionImpl getCollection(String collectionName);
}
