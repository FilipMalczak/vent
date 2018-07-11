package com.github.filipmalczak.vent.api.general;

import com.github.filipmalczak.vent.api.general.query.QueryBuilder;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.temporal.TemporallyEnabled;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public interface VentCollection<
    SingleSuccess, SingleId, SingleConfirmation, SingleSnapshot,
    ManyIds, ManySnapshots,
    QueryBuilderImpl extends QueryBuilder> extends TemporallyEnabled {
    String getVentCollectionName();

    SingleSuccess drop();

    // todo from here
    SingleId create(Map initialState);

    default SingleId create(){
        return create(new HashMap());
    }

    SingleConfirmation update(VentId id, Map newState);
    SingleConfirmation delete(VentId id);

    SingleSnapshot get(VentId id, LocalDateTime queryAt);

    SingleConfirmation putValue(VentId id, String path, Object value);
    SingleConfirmation deleteValue(VentId id, String path);

    //todo to here should be extracted to VentObject facade
    //todo figure out Query#delete()/deleteAll() without TemporalService

    //todo: could use count()
    ManyIds identifyAll(LocalDateTime queryAt);

    ManySnapshots getAll(LocalDateTime queryAt);

    QueryBuilderImpl queryBuilder();
}
