package com.github.filipmalczak.vent.api.blocking;

import com.github.filipmalczak.vent.traits.paradigm.Blocking;



public interface BlockingVentDb extends Blocking {
    BlockingVentCollection getCollection(String collectionName);
}
