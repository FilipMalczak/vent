package com.github.filipmalczak.vent.embedded;

import com.github.filipmalczak.vent.api.general.query.CriteriaBuilder;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveQueryBuilder;
import com.github.filipmalczak.vent.embedded.query.AndCriteriaBuilder;
import com.github.filipmalczak.vent.embedded.query.EmbeddedReactiveQuery;
import com.github.filipmalczak.vent.embedded.service.MongoQueryPreparator;
import com.github.filipmalczak.vent.embedded.service.SnapshotService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import java.util.function.Consumer;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class EmbeddedReactiveQueryBuilder implements ReactiveQueryBuilder<EmbeddedReactiveQueryBuilder, EmbeddedReactiveQuery> {
    private @NonNull
    String collectionName;
    private @NonNull
    AndCriteriaBuilder rootCriteriaBuilder;
    private @NonNull
    MongoQueryPreparator mongoQueryPreparator;
    private @NonNull
    ReactiveMongoTemplate mongoTemplate;
    private @NonNull
    SnapshotService snapshotService;


    @Override
    public EmbeddedReactiveQueryBuilder and(Consumer<CriteriaBuilder> andScope) {
        rootCriteriaBuilder.and(andScope);
        return this;
    }

    @Override
    public EmbeddedReactiveQueryBuilder or(Consumer<CriteriaBuilder> orScope) {
        rootCriteriaBuilder.or(orScope);
        return this;
    }

    @Override
    public EmbeddedReactiveQueryBuilder not(Consumer<CriteriaBuilder> notScope) {
        rootCriteriaBuilder.not(notScope);
        return this;
    }

    @Override
    public EmbeddedReactiveQueryBuilder equals(String path, Object value) {
        rootCriteriaBuilder.equals(path, value);
        return this;
    }

    @Override
    public EmbeddedReactiveQuery build() {
        return new EmbeddedReactiveQuery(collectionName, rootCriteriaBuilder.toOperator(), mongoQueryPreparator, mongoTemplate, snapshotService);
    }
}
