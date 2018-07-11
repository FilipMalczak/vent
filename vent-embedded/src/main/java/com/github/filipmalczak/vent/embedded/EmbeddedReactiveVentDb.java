package com.github.filipmalczak.vent.embedded;

import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.embedded.model.events.impl.EventFactory;
import com.github.filipmalczak.vent.embedded.service.CollectionService;
import com.github.filipmalczak.vent.embedded.service.MongoQueryPreparator;
import com.github.filipmalczak.vent.embedded.service.PageService;
import com.github.filipmalczak.vent.embedded.service.SnapshotService;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

//todo define API status for embedded stuff; probably provide single factory-like entry point
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class EmbeddedReactiveVentDb implements ReactiveVentDb {
    private @NonNull PageService pageService;

    private @NonNull EventFactory eventFactory;

    private @NonNull SnapshotService snapshotService;

    private @NonNull MongoQueryPreparator mongoQueryPreparator;

    private @NonNull CollectionService collectionService;

    private @NonNull ReactiveMongoOperations mongoOperations;

    private static final String VENT_DESCRIPTOR_COLLECTION = "vent.descriptor";
    private AtomicBoolean initialized = new AtomicBoolean(false);

    @Override
    public ReactiveVentCollection getCollection(String collectionName) {
        //fixme: ugly
        collectionService.manage(collectionName).block();
        return new EmbeddedReactiveVentCollection(collectionName, pageService, eventFactory, snapshotService, mongoQueryPreparator, collectionService, mongoOperations);
    }

    @Override
    public Mono<Success> optimizePages(SuggestionStrength strength, OptimizationType type) {
        //todo muthafucking TODO
        return null;
    }

    @Override
    public Flux<String> getManagedCollections() {
        return collectionService.getAllCollectionNames();
    }

    @Override
    public Mono<Success> manage(String collectionName) {
        return collectionService.manage(collectionName);
    }

    @Override
    public TemporalService getTemporalService() {
        return pageService.getTemporalService();
    }
}
