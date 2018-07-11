package com.github.filipmalczak.vent.embedded;

import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.embedded.model.events.impl.EventFactory;
import com.github.filipmalczak.vent.embedded.service.*;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@NoArgsConstructor
public class EmbeddedReactiveVentFactory {
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

    public EmbeddedReactiveVentFactory pageService(@NonNull PageServiceCreator pageService) {
        this.pageService = pageService;
        return this;
    }

    public EmbeddedReactiveVentFactory eventFactory(@NonNull EventFactoryCreator eventFactory) {
        this.eventFactory = eventFactory;
        return this;
    }

    public EmbeddedReactiveVentFactory snapshotService(@NonNull SnapshotServiceCreator snapshotService) {
        this.snapshotService = snapshotService;
        return this;
    }

    public EmbeddedReactiveVentFactory mongoQueryPreparator(@NonNull MongoQueryPreparatorCreator mongoQueryPreparator) {
        this.mongoQueryPreparator = mongoQueryPreparator;
        return this;
    }

    public EmbeddedReactiveVentFactory temporalService(@NonNull TemporalServiceCreator temporalService) {
        this.temporalService = temporalService;
        return this;
    }

    public EmbeddedReactiveVentFactory snapshotRenderer(@NonNull SnapshotRendererCreator snapshotRenderer) {
        this.snapshotRenderer = snapshotRenderer;
        return this;
    }

    public EmbeddedReactiveVentFactory collectionService(CollectionServiceCreator collectionService) {
        this.collectionService = collectionService;
        return this;
    }

    public EmbeddedReactiveVentFactory reactiveMongoOperations(@NonNull ReactiveMongoOperationsCreator reactiveMongoOperations) {
        this.reactiveMongoOperations = reactiveMongoOperations;
        return this;
    }

    public EmbeddedReactiveVentDb newInstance(){
        ReactiveMongoOperations operations = nonNull(reactiveMongoOperations.get());
        TemporalService temporal = nonNull(temporalService.get());
        EventFactory evFactory = nonNull(eventFactory.get());
        PageService page = nonNull(pageService.create(temporal, operations, evFactory));
        SnapshotRenderer renderer = nonNull(snapshotRenderer.get());
        SnapshotService snapshot = nonNull(snapshotService.apply(renderer, page));
        CollectionService collection = nonNull(collectionService.apply(operations, temporal));
        MongoQueryPreparator preparator = nonNull(mongoQueryPreparator.get());
        return new EmbeddedReactiveVentDb(
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
