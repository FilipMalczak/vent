package com.github.filipmalczak.vent.api.blocking;

import com.github.filipmalczak.vent.api.ObjectSnapshot;
import com.github.filipmalczak.vent.api.query.VentQuery;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentQuery;
import com.github.filipmalczak.vent.traits.Blocking;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public interface BlockingVentQuery extends Blocking<ReactiveVentQuery>, VentQuery<Stream<ObjectSnapshot>, Long, Boolean> {
    Stream<ObjectSnapshot> find(LocalDateTime queryAt);

    default Long count(LocalDateTime queryAt){
        return find(queryAt).count();
    }

    default Boolean exists(LocalDateTime queryAt){
        return find(queryAt).findAny().isPresent();
    }
}
