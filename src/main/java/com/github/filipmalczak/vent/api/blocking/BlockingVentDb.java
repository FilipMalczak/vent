package com.github.filipmalczak.vent.api.blocking;

public interface BlockingVentDb {
    BlockingVentCollection getCollection(String collectionName);
}
