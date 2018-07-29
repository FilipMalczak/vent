package com.github.filipmalczak.vent.mongo;

import com.github.filipmalczak.vent.api.general.query.CriteriaBuilder;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveQueryBuilder;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.mongo.query.AndCriteriaBuilder;
import com.github.filipmalczak.vent.mongo.query.VentQuery;
import com.github.filipmalczak.vent.mongo.service.CollectionService;
import com.github.filipmalczak.vent.mongo.service.MongoQueryPreparator;
import com.github.filipmalczak.vent.mongo.service.SnapshotService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;

import java.util.function.Consumer;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class VentQueryBuilder implements ReactiveQueryBuilder<VentQueryBuilder, VentQuery> {
    private @NonNull String collectionName;
    private @NonNull AndCriteriaBuilder rootCriteriaBuilder;
    private @NonNull MongoQueryPreparator mongoQueryPreparator;
    private @NonNull ReactiveMongoOperations mongoOperations;
    private @NonNull SnapshotService snapshotService;
    private @NonNull CollectionService collectionService;
    private @NonNull TemporalService temporalService;


    @Override
    public VentQueryBuilder and(Consumer<CriteriaBuilder> andScope) {
        rootCriteriaBuilder.and(andScope);
        return this;
    }

    @Override
    public VentQueryBuilder or(Consumer<CriteriaBuilder> orScope) {
        rootCriteriaBuilder.or(orScope);
        return this;
    }

    @Override
    public VentQueryBuilder not(Consumer<CriteriaBuilder> notScope) {
        rootCriteriaBuilder.not(notScope);
        return this;
    }

    @Override
    public VentQueryBuilder equals(String path, Object value) {
        rootCriteriaBuilder.equals(path, value);
        return this;
    }

    @Override
    public VentQuery build() {
        return new VentQuery(collectionName, rootCriteriaBuilder.toOperator(), mongoQueryPreparator, mongoOperations, snapshotService, collectionService, temporalService);
    }
}
