package com.github.filipmalczak.vent.mongo;

import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.mongo.model.events.impl.EventFactory;
import com.github.filipmalczak.vent.mongo.query.VentQuery;
import com.github.filipmalczak.vent.mongo.service.CollectionService;
import com.github.filipmalczak.vent.mongo.service.PageService;
import com.github.filipmalczak.vent.mongo.service.SnapshotService;
import com.github.filipmalczak.vent.mongo.service.VentServices;
import com.github.filipmalczak.vent.mongo.service.query.preparator.MongoQueryPreparator;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//todo define API status for mongo stuff; probably provide single factory-like entry point
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class VentDb implements ReactiveVentDb<VentCollection, VentQueryBuilder, VentQuery> {
    private @NonNull PageService pageService;

    private @NonNull EventFactory eventFactory;

    private @NonNull SnapshotService snapshotService;

    private @NonNull MongoQueryPreparator mongoQueryPreparator;

    private @NonNull CollectionService collectionService;

    private @NonNull ReactiveMongoOperations mongoOperations;

    public VentDb(VentServices ventServices) {
        this(
            ventServices.getPageService(),
            ventServices.getEventFactory(),
            ventServices.getSnapshotService(),
            ventServices.getQueryPreparator(),
            ventServices.getCollectionService(),
            ventServices.getMongoOperations()
        );
    }

    @Override
    public VentCollection getCollection(String collectionName) {
        //fixme: ugly
        collectionService.manage(collectionName).subscribe();
        return new VentCollection(collectionName, pageService, eventFactory, snapshotService, mongoQueryPreparator, collectionService, mongoOperations);
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
