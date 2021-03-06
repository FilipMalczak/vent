package com.github.filipmalczak.vent.mongo;

import com.github.filipmalczak.vent.api.model.EventConfirmation;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.mongo.model.Page;
import com.github.filipmalczak.vent.mongo.model.events.Event;
import com.github.filipmalczak.vent.mongo.model.events.impl.EventFactory;
import com.github.filipmalczak.vent.mongo.query.AndCriteriaBuilder;
import com.github.filipmalczak.vent.mongo.query.VentQuery;
import com.github.filipmalczak.vent.mongo.service.CollectionService;
import com.github.filipmalczak.vent.mongo.service.PageService;
import com.github.filipmalczak.vent.mongo.service.SnapshotService;
import com.github.filipmalczak.vent.mongo.service.query.preparator.MongoQueryPreparator;
import com.github.filipmalczak.vent.mongo.utils.MongoTranslator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Supplier;

@AllArgsConstructor
//todo builder?
public class VentCollection implements ReactiveVentCollection<VentQueryBuilder, VentQuery> {
    @Getter private @NonNull String ventCollectionName;

    private @NonNull PageService pageService;

    private @NonNull EventFactory eventFactory;

    private @NonNull SnapshotService snapshotService;

    private @NonNull MongoQueryPreparator mongoQueryPreparator;

    private @NonNull CollectionService collectionService;

    private @NonNull ReactiveMongoOperations mongoOperations;

    @Override
    public Mono<Success> drop() {
        //fixme this is ugly, it shouldnt be pageServices responsibility, but I don't want dependency on template
        return collectionService.mongoCollectionName(ventCollectionName).flatMap(r -> pageService.drop(r.getName()));
    }

    @Override
    public Mono<VentId> create(Map initialState) {
        return collectionService.mongoCollectionName(ventCollectionName).flatMap(r ->
            pageService.
                createFirstPage(r.getName(), r.getNow(), initialState).
                map(Page::getObjectId).
                map(MongoTranslator::fromMongo)
        );
    }

    @Override
    public Mono<EventConfirmation> putValue(VentId id, String path, Object value){
        return addEventToCurrentPage(id, eventFactory.putValue(path, value));
    }

    @Override
    public Mono<EventConfirmation> deleteValue(VentId id, String path) {
        return addEventToCurrentPage(id, eventFactory.deleteValue(path));
    }


    @Override
    public Mono<ObjectSnapshot> get(VentId id, Supplier<LocalDateTime> queryAt) {
        return collectionService.mongoCollectionName(ventCollectionName, queryAt).flatMap(r ->
            snapshotService.
                getSnapshot(r.getName(), id, r.getNow())
        );
    }

    @Override
    public Flux<VentId> identifyAll(Supplier<LocalDateTime> queryAt) {
        return collectionService.mongoCollectionName(ventCollectionName, queryAt).flux().flatMap(r ->
            pageService.allPages(
                    r.getName(),
                    r.getNow()
                ).
                map(Page::getObjectId).
                map(MongoTranslator::fromMongo)
        );
    }

    @Override
    public Mono<EventConfirmation> update(VentId id, Map newState) {
        //todo analyse impact to criteria for Equals, see com.github.filipmalczak.vent.api.query.operator.EqualsOperator
        //todo check whether query can be optimized with this approach
        return addEventToNewPage(id, eventFactory.update(newState));
    }

    @Override
    public Mono<EventConfirmation> delete(@NonNull VentId id) {
        return collectionService.mongoCollectionName(ventCollectionName).flatMap(r ->
            pageService.
                currentPage(r.getName(), id).
                flatMap(p ->
                    pageService.delete(r.getName(), r.getNow(), p)
                )
        );
    }

    @Override
    public VentQueryBuilder queryBuilder() {
        return new VentQueryBuilder(ventCollectionName, new AndCriteriaBuilder(), mongoQueryPreparator, mongoOperations, snapshotService, collectionService, getTemporalService());
    }

    private Mono<EventConfirmation> addEventToNewPage(VentId id, Event event){
        return collectionService.mongoCollectionName(ventCollectionName).flatMap(r ->
            pageService.
                createEmptyNextPage(r.getName(), id, r.getNow()).
                flatMap(p -> pageService.addEvent(r.getName(), p, r.align(event)))
        );
    }

    private Mono<EventConfirmation> addEventToCurrentPage(VentId id, Event event){
        return collectionService.mongoCollectionName(ventCollectionName).flatMap(r ->
            pageService.
                currentPage(r.getName(), id).
                flatMap(p -> pageService.addEvent(r.getName(), p, r.align(event)))
        );
    }

    @Override
    public TemporalService getTemporalService() {
        return pageService.getTemporalService();
    }
}
