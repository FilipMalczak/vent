package com.github.filipmalczak.vent.embedded;

import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.embedded.model.events.impl.EventFactory;
import com.github.filipmalczak.vent.embedded.service.MongoQueryPreparator;
import com.github.filipmalczak.vent.embedded.service.PageService;
import com.github.filipmalczak.vent.embedded.service.SnapshotService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;

//todo define API status for embedded stuff; probably provide single factory-like entry point
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EmbeddedReactiveVentDb implements ReactiveVentDb {
    private @NonNull PageService pageService;

    private @NonNull EventFactory eventFactory;

    private @NonNull SnapshotService snapshotService;

    private @NonNull
    MongoQueryPreparator mongoQueryPreparator;

    private @NonNull
    ReactiveMongoTemplate mongoTemplate;

    @Override
    public ReactiveVentCollection getCollection(String collectionName) {
        return new EmbeddedReactiveVentCollection(collectionName, pageService, eventFactory, snapshotService, mongoQueryPreparator, mongoTemplate);
    }
}
