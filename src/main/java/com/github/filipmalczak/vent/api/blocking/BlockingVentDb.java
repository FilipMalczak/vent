package com.github.filipmalczak.vent.api.blocking;

import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.api.traits.Blocking;

public interface BlockingVentDb extends Blocking<ReactiveVentDb> {
    BlockingVentCollection getCollection(String collectionName);
}
