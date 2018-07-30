package com.github.filipmalczak.vent.api.general;

import com.github.filipmalczak.vent.api.general.defaults.CompositeCollection;
import com.github.filipmalczak.vent.api.general.query.QueryBuilder;
import com.github.filipmalczak.vent.api.general.query.VentQuery;
import com.github.filipmalczak.vent.api.temporal.TemporallyEnabled;
import lombok.NonNull;

//todo split trait-dependent VentOperations and always-blocking VentManagement (like getManagedCollections()) (should I?)
public interface VentDb<
            SingleBoolean, SingleSuccess, SingleId, SingleConfirmation, SingleSnapshot,
            ManyStrings, ManyIds, ManySnapshots,
            FindResult, CountResult, ExistsResult,
            CollectionImpl extends VentCollection<
                SingleSuccess, SingleId, SingleConfirmation, SingleSnapshot,
                ManyIds, ManySnapshots,
                FindResult, CountResult, ExistsResult,
                QueryBuilderImpl, QueryImpl
                >,
            QueryBuilderImpl extends QueryBuilder<FindResult, CountResult, ExistsResult, QueryBuilderImpl, QueryImpl>,
            QueryImpl extends VentQuery<FindResult, CountResult, ExistsResult>
        > extends TemporallyEnabled {

    /**
     * I've arbitrally decided that it's gonna be more common to implement read and write stacks together. Because of
     * that default implementation of getCollectionForReading/-Writing return result of this method.
     *
     * If your Vent implementation handles read and write stacks in different ways, you can easily merge them into full
     * collection by using CompositeCollection.
     *
     * @see CompositeCollection
     */
    CollectionImpl getCollection(String collectionName);

    default VentCollectionReadOperations<SingleSnapshot, ManyIds, ManySnapshots, QueryBuilderImpl> getCollectionForReading(String collectionName) {
        return getCollection(collectionName);
    }

    default VentCollectionWriteOperations<SingleSuccess, SingleId, SingleConfirmation> getCollectionForWriting(String collectionName) {
        return getCollection(collectionName);
    }

    default SingleSuccess drop(String collectionName){
        return getCollectionForWriting(collectionName).drop();
    }

    //todo these will possibly move to some kind of exposed ventDbDescriptor concept; it may be related to mongo model class, but doesnt have to
    ManyStrings getManagedCollections();
    SingleBoolean isManaged(@NonNull String collectionName);
    SingleSuccess manage(String collectionName);
}
