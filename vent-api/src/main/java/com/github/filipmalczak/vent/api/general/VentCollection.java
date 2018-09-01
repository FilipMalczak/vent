package com.github.filipmalczak.vent.api.general;

import com.github.filipmalczak.vent.api.general.defaults.CompositeFacade;
import com.github.filipmalczak.vent.api.general.object.VentObjectFacade;
import com.github.filipmalczak.vent.api.general.query.QueryBuilder;
import com.github.filipmalczak.vent.api.general.query.VentQuery;
import com.github.filipmalczak.vent.api.model.VentId;

public interface VentCollection<
        SingleSuccess, SingleId, SingleConfirmation, SingleSnapshot,
        ManyIds, ManySnapshots,
        FindResult, CountResult, ExistsResult,
        QueryBuilderImpl extends QueryBuilder<
            FindResult, CountResult, ExistsResult,
            QueryBuilderImpl, QueryImpl
        >,
        QueryImpl extends VentQuery<FindResult, CountResult, ExistsResult>
    > extends
        VentCollectionReadOperations<SingleSnapshot, ManyIds, ManySnapshots, QueryBuilderImpl>,
        VentCollectionWriteOperations<SingleSuccess, SingleId, SingleConfirmation> {

    //todo create() is needed here (with default overload with empty map); probably should add createObject(collectionName) to db, and keep DB reference in CompositeCollection

    default VentObjectFacade<SingleConfirmation, SingleSnapshot> getFacade(VentId ventId) {
        return new CompositeFacade<>(ventId, getVentCollectionName(), getReadFacade(ventId), getWriteFacade(ventId));
    }
}
