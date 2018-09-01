package com.github.filipmalczak.vent.api.blocking;

import com.github.filipmalczak.vent.api.blocking.query.BlockingQueryBuilder;
import com.github.filipmalczak.vent.api.blocking.query.BlockingVentQuery;
import com.github.filipmalczak.vent.api.general.VentCollection;
import com.github.filipmalczak.vent.api.model.EventConfirmation;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.traits.paradigm.Blocking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


public interface BlockingVentCollection<
            QueryBuilderImpl extends BlockingQueryBuilder<QueryBuilderImpl, QueryImpl>,
            QueryImpl extends BlockingVentQuery
        > extends VentCollection<
            Success, VentId, EventConfirmation, ObjectSnapshot,
            Stream<VentId>, Stream<ObjectSnapshot>,
            Stream<ObjectSnapshot>, Long, Boolean,
            QueryBuilderImpl, QueryImpl
        >, Blocking {
    Success drop();

    default Stream<ObjectSnapshot> getAll(Supplier<LocalDateTime> queryAt){
        return identifyAll(queryAt).map(id -> get(id, queryAt));
    }

    //todo add non-supplier variants of methods below

    default List<VentId> listAllIds(Supplier<LocalDateTime> queryAt){
        return identifyAll(queryAt).collect(toList());
    }

    default List<ObjectSnapshot> listAll(Supplier<LocalDateTime> queryAt){
        return getAll(queryAt).collect(toList());
    }
}
