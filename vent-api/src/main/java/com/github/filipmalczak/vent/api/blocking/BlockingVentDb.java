package com.github.filipmalczak.vent.api.blocking;

import com.github.filipmalczak.vent.api.general.VentDb;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.traits.paradigm.Blocking;

import java.util.stream.Stream;

public interface BlockingVentDb extends VentDb<BlockingVentCollection, Stream<String>, Success, Boolean>, Blocking {
    BlockingVentCollection getCollection(String collectionName);
}
