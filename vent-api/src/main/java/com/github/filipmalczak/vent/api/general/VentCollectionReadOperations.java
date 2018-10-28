package com.github.filipmalczak.vent.api.general;

import com.github.filipmalczak.vent.api.general.defaults.ObjectReadFacade;
import com.github.filipmalczak.vent.api.general.object.VentObjectReadFacade;
import com.github.filipmalczak.vent.api.general.query.QueryBuilder;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.api.temporal.TemporallyEnabled;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public interface VentCollectionReadOperations<
    SingleSnapshot,
    ManyIds, ManySnapshots,
    QueryBuilderImpl extends QueryBuilder> extends TemporallyEnabled {
    String getVentCollectionName();

    SingleSnapshot get(VentId id, Supplier<LocalDateTime> queryAt);

    default SingleSnapshot get(VentId id){
        return get(id, getTemporalService());
    }

    default SingleSnapshot get(VentId id, LocalDateTime queryAt){
        return get(id, () -> queryAt);
    }

    //todo: could use count(<at>) - it would require new generic param - SingleLong/CountResult
    ManyIds identifyAll(Supplier<LocalDateTime> queryAt);

    default ManyIds identifyAll(){
        return identifyAll(getTemporalService()::now);
    }

    default ManyIds identifyAll(LocalDateTime queryAt){
        return identifyAll(() -> queryAt);
    }

    ManySnapshots getAll(Supplier<LocalDateTime> queryAt);

    default ManySnapshots getAll(TemporalService temporalService){
        return getAll((Supplier<LocalDateTime>) temporalService);
    }

    default ManySnapshots getAll(){
        return getAll(getTemporalService()::now);
    }

    default ManySnapshots getAll(LocalDateTime queryAt){
        return getAll(() -> queryAt);
    }

    QueryBuilderImpl queryBuilder();

    default VentObjectReadFacade<SingleSnapshot> getReadFacade(VentId id){
        return new ObjectReadFacade<>(id, getVentCollectionName(), this);
    }
}
