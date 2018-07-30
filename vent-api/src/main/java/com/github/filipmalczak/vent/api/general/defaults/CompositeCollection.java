package com.github.filipmalczak.vent.api.general.defaults;

import com.github.filipmalczak.vent.api.general.VentCollection;
import com.github.filipmalczak.vent.api.general.VentCollectionReadOperations;
import com.github.filipmalczak.vent.api.general.VentCollectionWriteOperations;
import com.github.filipmalczak.vent.api.general.object.VentObjectFacade;
import com.github.filipmalczak.vent.api.general.object.VentObjectReadFacade;
import com.github.filipmalczak.vent.api.general.object.VentObjectWriteFacade;
import com.github.filipmalczak.vent.api.general.query.QueryBuilder;
import com.github.filipmalczak.vent.api.general.query.VentQuery;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
public class CompositeCollection<
            SingleSuccess, SingleId, SingleConfirmation, SingleSnapshot,
            ManyIds, ManySnapshots,
            QueryBuilderImpl extends QueryBuilder<
                FindResult, CountResult, ExistsResult,
                QueryBuilderImpl, QueryImpl
            >,
            QueryImpl extends VentQuery<FindResult, CountResult, ExistsResult>,
            FindResult, CountResult, ExistsResult
            > implements VentCollection<
                SingleSuccess, SingleId, SingleConfirmation, SingleSnapshot,
                ManyIds, ManySnapshots,
                FindResult, CountResult, ExistsResult,
                QueryBuilderImpl, QueryImpl
        > {
    @Getter private String ventCollectionName;
    private VentCollectionReadOperations<SingleSnapshot, ManyIds, ManySnapshots, QueryBuilderImpl> readOperations;
    private VentCollectionWriteOperations<SingleSuccess, SingleId, SingleConfirmation> writeOperations;

    @Override
    public VentObjectReadFacade<SingleSnapshot> getReadFacade(VentId ventId) {
        return readOperations.getReadFacade(ventId);
    }

    @Override
    public VentObjectWriteFacade<SingleConfirmation> getWriteFacade(VentId ventId) {
        return writeOperations.getWriteFacade(ventId);
    }

    @Override
    public VentObjectFacade<SingleConfirmation, SingleSnapshot> getFacade(VentId ventId) {
        return new CompositeFacade<>(ventId, getVentCollectionName(), getReadFacade(ventId), getWriteFacade(ventId));
    }

    @Override
    public SingleSnapshot get(VentId id, LocalDateTime queryAt) {
        return readOperations.get(id, queryAt);
    }

    @Override
    public ManyIds identifyAll(LocalDateTime queryAt) {
        return readOperations.identifyAll(queryAt);
    }

    @Override
    public ManySnapshots getAll(LocalDateTime queryAt) {
        return readOperations.getAll(queryAt);
    }

    @Override
    public QueryBuilderImpl queryBuilder() {
        return readOperations.queryBuilder();
    }

    @Override
    public SingleSuccess drop() {
        return writeOperations.drop();
    }

    @Override
    public SingleId create(Map initialState) {
        return writeOperations.create(initialState);
    }

    @Override
    public SingleConfirmation update(VentId id, Map newState) {
        return writeOperations.update(id, newState);
    }

    @Override
    public SingleConfirmation delete(VentId id) {
        return writeOperations.delete(id);
    }

    @Override
    public SingleConfirmation putValue(VentId id, String path, Object value) {
        return writeOperations.putValue(id, path, value);
    }

    @Override
    public SingleConfirmation deleteValue(VentId id, String path) {
        return writeOperations.deleteValue(id, path);
    }

    @Override
    public TemporalService getTemporalService() {
        return readOperations.getTemporalService();
    }
}
