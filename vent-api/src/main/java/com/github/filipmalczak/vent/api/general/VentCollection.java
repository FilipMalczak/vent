package com.github.filipmalczak.vent.api.general;

import com.github.filipmalczak.vent.api.general.query.QueryBuilder;
import com.github.filipmalczak.vent.api.general.query.VentQuery;
import com.github.filipmalczak.vent.api.model.VentId;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public interface VentCollection<
    SingleSuccess, SingleId, SingleConfirmation, SingleSnapshot,
    ManyIds, ManySnapshots,
    QueryBuilderImpl extends QueryBuilder> {
    String getName();

    SingleSuccess drop();

    SingleId create(Map initialState);

    default SingleId create(){
        return create(new HashMap()); //hashmap instead of Struct.map to avoid future dependency between API and utils
    }

    SingleConfirmation putValue(VentId id, String path, Object value);

    SingleConfirmation deleteValue(VentId id, String path);

    SingleConfirmation update(VentId id, Map newState);

    SingleConfirmation delete(VentId id);
    //todo figure out Query#delete()/deleteAll() without TemporalService

    SingleSnapshot get(VentId id, LocalDateTime queryAt);

    ManyIds identifyAll(LocalDateTime queryAt);

    ManySnapshots getAll(LocalDateTime queryAt);

    QueryBuilderImpl queryBuilder();
}
