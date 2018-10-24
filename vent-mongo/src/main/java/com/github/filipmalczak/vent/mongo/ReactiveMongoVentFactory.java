package com.github.filipmalczak.vent.mongo;

import com.github.filipmalczak.vent.api.temporal.SimpleTemporalService;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.mongo.extension.optimization.Defaults;
import com.github.filipmalczak.vent.mongo.factory.PluggableFactory;
import com.github.filipmalczak.vent.mongo.factory.ResultWithAPI;
import com.github.filipmalczak.vent.mongo.model.events.impl.EventFactory;
import com.github.filipmalczak.vent.mongo.service.*;
import com.github.filipmalczak.vent.mongo.service.query.preparator.MongoQueryPreparator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;

import java.util.function.BiFunction;
import java.util.function.Supplier;

@NoArgsConstructor
@Slf4j
public class ReactiveMongoVentFactory extends PluggableFactory<ReactiveMongoVentFactory, VentDb, ReactiveMongoVentFactory.VentServiceCreators, VentServices> {
    private boolean useDefaultOptimization = true;

    public ReactiveMongoVentFactory withDefaultOptimization(boolean val){
        useDefaultOptimization = val;
        return this;
    }

    public ReactiveMongoVentFactory withDefaultOptimization(){
        return withDefaultOptimization(true);
    }

    public ReactiveMongoVentFactory withoutDefaultOptimization(){
        return withDefaultOptimization(false);
    }

    @Override
    public ResultWithAPI<VentDb, VentServices> prepare(VentServiceCreators creators) {
        if (useDefaultOptimization){
            plugins(Defaults.defaultOptimizations);
        } else {
            //todo property to disable warning/scan plugins to see if optimization is enabled
            log.warn("Default Vent optimizations are turned off! Make sure that you take care of that manually!");
        }
        ReactiveMongoOperations operations = nonNull(creators.reactiveMongoOperations.get());
        log.debug("ReactiveMongoOperations: " + operations);
        TemporalService temporal = nonNull(creators.temporalService.get());
        log.debug("TemporalService: " + temporal);
        EventFactory evFactory = nonNull(creators.eventFactory.get());
        log.debug("EventFactory: " + evFactory);
        PageService page = nonNull(creators.pageService.create(temporal, operations, evFactory));
        log.debug("PageService: " + page);
        SnapshotRenderer renderer = nonNull(creators.snapshotRenderer.get());
        log.debug("SnapshotRenderer: " + renderer);
        SnapshotService snapshot = nonNull(creators.snapshotService.apply(renderer, page));
        log.debug("SnapshotService: " + snapshot);
        CollectionService collection = nonNull(creators.collectionService.apply(operations, temporal));
        log.debug("CollectionService: " + collection);
        MongoQueryPreparator preparator = nonNull(creators.mongoQueryPreparator.get());
        log.debug("MongoQueryPreparator: " + preparator);
        VentServices services = new VentServices(operations, temporal, evFactory, page, renderer, snapshot, collection, preparator);
        VentDb ventDb = new VentDb(services);
        return ResultWithAPI.of(ventDb, services);
    }

    private <T> T nonNull(@NonNull T val) {
        return val;
    }

    @FunctionalInterface
    public interface PageServiceCreator {
        PageService create(TemporalService temporalService, ReactiveMongoOperations reactiveMongoOperations,
                           EventFactory eventFactory);
    }

    @FunctionalInterface
    public interface EventFactoryCreator extends Supplier<EventFactory> {
    }

    @FunctionalInterface
    public interface SnapshotServiceCreator extends BiFunction<SnapshotRenderer, PageService, SnapshotService> {
    }

    @FunctionalInterface
    public interface MongoQueryPreparatorCreator extends Supplier<MongoQueryPreparator> {
    }

    @FunctionalInterface
    public interface TemporalServiceCreator extends Supplier<TemporalService> {
    }

    @FunctionalInterface
    public interface SnapshotRendererCreator extends Supplier<SnapshotRenderer> {
    }

    @FunctionalInterface
    public interface CollectionServiceCreator extends BiFunction<ReactiveMongoOperations, TemporalService, CollectionService> {
    }

    @FunctionalInterface
    public interface ReactiveMongoOperationsCreator extends Supplier<ReactiveMongoOperations> {
    }

    /**
     * Most of fields are not a public API. Package-private visibility is intended.
     */
    @Builder(builderMethodName = "configure")
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class VentServiceCreators {
        @Builder.Default
        @NonNull
        PageServiceCreator pageService = PageService::new;
        @Builder.Default
        @NonNull
        EventFactoryCreator eventFactory = EventFactory::new;
        @Builder.Default
        @NonNull
        SnapshotServiceCreator snapshotService = SnapshotService::new;
        @Builder.Default
        @NonNull
        MongoQueryPreparatorCreator mongoQueryPreparator = MongoQueryPreparator::new;
        @Builder.Default
        @NonNull
        SnapshotRendererCreator snapshotRenderer = NaiveSnapshotRenderer::new;
        @Builder.Default
        @NonNull
        CollectionServiceCreator collectionService = CollectionService::new;

        //the only really customizable field
        @Builder.Default
        @NonNull
        TemporalServiceCreator temporalService = SimpleTemporalService::new;

        //the only truly required field
        @NonNull
        ReactiveMongoOperationsCreator reactiveMongoOperations;
    }
}
