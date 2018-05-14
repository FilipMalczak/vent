package com.github.filipmalczak.vent.api.blocking;

import com.github.filipmalczak.vent.api.blocking.query.BlockingQueryBuilder;
import com.github.filipmalczak.vent.api.blocking.query.BlockingVentQuery;
import com.github.filipmalczak.vent.api.general.VentCollection;
import com.github.filipmalczak.vent.api.model.EventConfirmation;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.traits.Blocking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


public interface BlockingVentCollection extends VentCollection<
    Success, VentId, EventConfirmation, ObjectSnapshot, Stream<VentId>, Stream<ObjectSnapshot>, BlockingQueryBuilder<?, ? extends BlockingVentQuery>
    >, Blocking<ReactiveVentCollection> {
    Success drop();

    default Stream<ObjectSnapshot> getAll(LocalDateTime queryAt){
        return identifyAll(queryAt).map(id -> get(id, queryAt));
    }

    default List<VentId> listAllIds(LocalDateTime queryAt){
        return identifyAll(queryAt).collect(toList());
    }

    default List<ObjectSnapshot> listAll(LocalDateTime queryAt){
        return getAll(queryAt).collect(toList());
    }
}
