package com.github.filipmalczak.vent.mongo;

import com.github.filipmalczak.vent.api.temporal.SimpleTemporalService;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.mongo.model.events.impl.EventFactory;
import com.github.filipmalczak.vent.mongo.service.*;
import com.github.filipmalczak.vent.mongo.service.query.preparator.MongoQueryPreparator;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;

import java.util.function.BiFunction;
import java.util.function.Supplier;

@NoArgsConstructor
@Slf4j
public class ReactiveMongoVentFactory {
    @FunctionalInterface
    public interface PageServiceCreator {
        PageService create(TemporalService temporalService, ReactiveMongoOperations reactiveMongoOperations,
                           EventFactory eventFactory);
    }

    @FunctionalInterface
    public interface EventFactoryCreator extends Supplier<EventFactory> {}

    @FunctionalInterface
    public interface SnapshotServiceCreator extends BiFunction<SnapshotRenderer, PageService, SnapshotService> {}

    @FunctionalInterface
    public interface MongoQueryPreparatorCreator extends Supplier<MongoQueryPreparator> {}

    @FunctionalInterface
    public interface TemporalServiceCreator extends Supplier<TemporalService> {}

    @FunctionalInterface
    public interface SnapshotRendererCreator extends Supplier<SnapshotRenderer> {}

    @FunctionalInterface
    public interface CollectionServiceCreator extends BiFunction<ReactiveMongoOperations, TemporalService, CollectionService> {}

    @FunctionalInterface
    public interface ReactiveMongoOperationsCreator extends Supplier<ReactiveMongoOperations> {}

    private PageServiceCreator pageService = PageService::new;
    private EventFactoryCreator eventFactory = EventFactory::new;
    private SnapshotServiceCreator snapshotService = SnapshotService::new;
    private MongoQueryPreparatorCreator mongoQueryPreparator = MongoQueryPreparator::new;
    private TemporalServiceCreator temporalService = SimpleTemporalService::new;
    private SnapshotRendererCreator snapshotRenderer =  NaiveSnapshotRenderer::new;
    private CollectionServiceCreator collectionService =  CollectionService::new;
    private ReactiveMongoOperationsCreator reactiveMongoOperations;

    public ReactiveMongoVentFactory pageService(@NonNull PageServiceCreator pageService) {
        this.pageService = pageService;
        return this;
    }

    public ReactiveMongoVentFactory eventFactory(@NonNull EventFactoryCreator eventFactory) {
        this.eventFactory = eventFactory;
        return this;
    }

    public ReactiveMongoVentFactory snapshotService(@NonNull SnapshotServiceCreator snapshotService) {
        this.snapshotService = snapshotService;
        return this;
    }

    public ReactiveMongoVentFactory mongoQueryPreparator(@NonNull MongoQueryPreparatorCreator mongoQueryPreparator) {
        this.mongoQueryPreparator = mongoQueryPreparator;
        return this;
    }

    public ReactiveMongoVentFactory temporalService(@NonNull TemporalServiceCreator temporalService) {
        this.temporalService = temporalService;
        return this;
    }

    public ReactiveMongoVentFactory snapshotRenderer(@NonNull SnapshotRendererCreator snapshotRenderer) {
        this.snapshotRenderer = snapshotRenderer;
        return this;
    }

    public ReactiveMongoVentFactory collectionService(CollectionServiceCreator collectionService) {
        this.collectionService = collectionService;
        return this;
    }

    public ReactiveMongoVentFactory reactiveMongoOperations(@NonNull ReactiveMongoOperationsCreator reactiveMongoOperations) {
        this.reactiveMongoOperations = reactiveMongoOperations;
        return this;
    }

    public VentDb newInstance(){
        ReactiveMongoOperations operations = nonNull(reactiveMongoOperations.get());
        log.debug("ReactiveMongoOperations: "+operations);
        TemporalService temporal = nonNull(temporalService.get());
        log.debug("TemporalService: "+temporal);
        EventFactory evFactory = nonNull(eventFactory.get());
        log.debug("EventFactory: "+evFactory);
        PageService page = nonNull(pageService.create(temporal, operations, evFactory));
        log.debug("PageService: "+page);
        SnapshotRenderer renderer = nonNull(snapshotRenderer.get());
        log.debug("SnapshotRenderer: "+renderer);
        SnapshotService snapshot = nonNull(snapshotService.apply(renderer, page));
        log.debug("SnapshotService: "+snapshot);
        CollectionService collection = nonNull(collectionService.apply(operations, temporal));
        log.debug("CollectionService: "+collection);
        MongoQueryPreparator preparator = nonNull(mongoQueryPreparator.get());
        log.debug("MongoQueryPreparator: "+preparator);
        return new VentDb(
            page,
            evFactory,
            snapshot,
            preparator,
            collection,
            operations
        );
    }

    private <T> T nonNull(@NonNull T val){return val;}
}
