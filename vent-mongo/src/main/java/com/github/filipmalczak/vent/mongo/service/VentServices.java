package com.github.filipmalczak.vent.mongo.service;

import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.api.temporal.TemporallyEnabled;
import com.github.filipmalczak.vent.mongo.model.events.impl.EventFactory;
import com.github.filipmalczak.vent.mongo.service.query.preparator.MongoQueryPreparator;
import lombok.Value;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;

@Value
public class VentServices implements TemporallyEnabled {
    ReactiveMongoOperations mongoOperations;
    TemporalService temporalService;
    EventFactory eventFactory;
    PageService pageService;
    SnapshotRenderer snapshotRenderer;
    SnapshotService snapshotService;
    CollectionService collectionService;
    MongoQueryPreparator queryPreparator;
}
