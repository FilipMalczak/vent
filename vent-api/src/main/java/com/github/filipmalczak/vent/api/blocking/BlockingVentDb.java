package com.github.filipmalczak.vent.api.blocking;

import com.github.filipmalczak.vent.api.blocking.query.BlockingQueryBuilder;
import com.github.filipmalczak.vent.api.blocking.query.BlockingVentQuery;
import com.github.filipmalczak.vent.api.general.VentDb;
import com.github.filipmalczak.vent.api.model.EventConfirmation;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.traits.paradigm.Blocking;

import java.util.stream.Stream;

public interface BlockingVentDb<
            CollectionImpl extends BlockingVentCollection<QueryBuilderImpl, QueryImpl>,
            QueryBuilderImpl extends BlockingQueryBuilder<QueryBuilderImpl, QueryImpl>,
            QueryImpl extends BlockingVentQuery
        > extends VentDb<
            Boolean, Success, VentId, EventConfirmation, ObjectSnapshot,
            Stream<String>, Stream<VentId>, Stream<ObjectSnapshot>,
            Stream<ObjectSnapshot>, Long, Boolean,
            CollectionImpl,
            QueryBuilderImpl, QueryImpl
        >, Blocking {
    CollectionImpl getCollection(String collectionName);
}
