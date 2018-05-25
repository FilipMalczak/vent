package com.github.filipmalczak.vent.api.blocking.query;

import com.github.filipmalczak.vent.api.general.query.VentQuery;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.traits.paradigm.Blocking;

import java.time.LocalDateTime;
import java.util.stream.Stream;


public interface BlockingVentQuery extends VentQuery<Stream<ObjectSnapshot>, Long, Boolean>, Blocking {
    Stream<ObjectSnapshot> find(LocalDateTime queryAt);

    default Long count(LocalDateTime queryAt){
        return find(queryAt).count();
    }

    default Boolean exists(LocalDateTime queryAt){
        return find(queryAt).findAny().isPresent();
    }
}
