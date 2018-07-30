package com.github.filipmalczak.vent.api.general;

import com.github.filipmalczak.vent.api.general.defaults.CompositeCollection;
import com.github.filipmalczak.vent.api.general.defaults.CompositeFacade;
import com.github.filipmalczak.vent.api.general.defaults.ObjectReadFacade;
import com.github.filipmalczak.vent.api.general.object.VentObjectReadFacade;
import com.github.filipmalczak.vent.api.general.query.QueryBuilder;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.temporal.TemporallyEnabled;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public interface VentCollectionReadOperations<
    SingleSnapshot,
    ManyIds, ManySnapshots,
    QueryBuilderImpl extends QueryBuilder> extends TemporallyEnabled {
    String getVentCollectionName();

    SingleSnapshot get(VentId id, LocalDateTime queryAt);

    //todo: could use count()
    ManyIds identifyAll(LocalDateTime queryAt);

    ManySnapshots getAll(LocalDateTime queryAt);

    QueryBuilderImpl queryBuilder();

    default VentObjectReadFacade<SingleSnapshot> getReadFacade(VentId id){
        return new ObjectReadFacade<>(id, getVentCollectionName(), this);
    }
}
