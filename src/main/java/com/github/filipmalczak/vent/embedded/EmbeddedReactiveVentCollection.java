package com.github.filipmalczak.vent.embedded;

import com.github.filipmalczak.vent.api.EventConfirmation;
import com.github.filipmalczak.vent.api.ObjectSnapshot;
import com.github.filipmalczak.vent.api.Success;
import com.github.filipmalczak.vent.api.VentId;
import com.github.filipmalczak.vent.api.blocking.BlockingVentCollection;
import com.github.filipmalczak.vent.api.blocking.BlockingVentQuery;
import com.github.filipmalczak.vent.api.query.BlockingQueryBuilder;
import com.github.filipmalczak.vent.api.query.ReactiveQueryBuilder;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentQuery;
import com.github.filipmalczak.vent.embedded.model.Page;
import com.github.filipmalczak.vent.embedded.model.events.Event;
import com.github.filipmalczak.vent.embedded.model.events.impl.EventFactory;
import com.github.filipmalczak.vent.embedded.query.AndCriteriaBuilder;
import com.github.filipmalczak.vent.embedded.service.MongoQueryPreparator;
import com.github.filipmalczak.vent.embedded.service.PageService;
import com.github.filipmalczak.vent.embedded.service.SnapshotService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

@AllArgsConstructor
//todo builder?
public class EmbeddedReactiveVentCollection implements ReactiveVentCollection {
    private @NonNull String collectionName;

    private @NonNull PageService pageService;

    private @NonNull EventFactory eventFactory;

    private @NonNull SnapshotService snapshotService;

    private @NonNull MongoQueryPreparator mongoQueryPreparator;

    private @NonNull ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Success> drop() {
        //fixme this is ugly, it shouldnt be pageServices responsibility, but I don't want dependency on template
        return pageService.drop(collectionName);
    }

    @Override
    public Mono<VentId> create(Map initialState) {
        return pageService.
            createFirstPage(collectionName, initialState).
            map(Page::getObjectId).
            map(VentId::fromMongoId);
    }

    @Override
    public Mono<EventConfirmation> putValue(VentId id, String path, Object value){
        return addEvent(id, eventFactory.putValue(path, value));
    }

    @Override
    public Mono<EventConfirmation> deleteValue(VentId id, String path) {
        return addEvent(id, eventFactory.deleteValue(path));
    }


    @Override
    public Mono<ObjectSnapshot> get(VentId id, LocalDateTime queryAt) {
        return snapshotService.getSnapshot(collectionName, id, queryAt);
    }

    @Override
    public Flux<VentId> identifyAll(LocalDateTime queryAt) {
        return pageService.allPages(collectionName, queryAt).map(Page::getObjectId).map(VentId::fromMongoId);
    }

    @Override
    public Mono<EventConfirmation> update(VentId id, Map newState) {
        //todo right after adding UPDATE event, new page should be created (with snapshot from right after UPDATE)
        //this will impact criteria for Equals, see com.github.filipmalczak.vent.api.query.operator.EqualsOperator
        return addEvent(id, eventFactory.update(newState));
    }

    @Override
    public ReactiveQueryBuilder<?, ? extends ReactiveVentQuery> queryBuilder() {
        return new EmbeddedReactiveQueryBuilder(collectionName, new AndCriteriaBuilder(), mongoQueryPreparator, mongoTemplate, snapshotService);
    }

    private Mono<EventConfirmation> addEvent(VentId id, Event event){
        return pageService.
            currentPage(collectionName, id).
            flatMap(p -> pageService.addEvent(collectionName, p, event));
    }

    @Override
    public BlockingVentCollection asBlocking() {
        return new BlockingVentCollection() {
            @Override
            public Success drop() {
                return asReactive().drop().block();
            }

            @Override
            public VentId create(Map initialState) {
                return asReactive().create(initialState).block();
            }

            @Override
            public EventConfirmation putValue(VentId id, String path, Object value) {
                return asReactive().putValue(id, path, value).block();
            }

            @Override
            public EventConfirmation deleteValue(VentId id, String path) {
                return asReactive().deleteValue(id, path).block();
            }

            @Override
            public ObjectSnapshot get(VentId id, LocalDateTime queryAt) {
                return asReactive().get(id, queryAt).block();
            }

            @Override
            public Stream<VentId> identifyAll(LocalDateTime queryAt) {
                return asReactive().identifyAll(queryAt).toStream();
            }

            @Override
            public EventConfirmation update(VentId id, Map newState) {
                return asReactive().update(id, newState).block();
            }

            @Override
            public BlockingQueryBuilder<?, ? extends BlockingVentQuery> queryBuilder() {
                return asReactive().queryBuilder().asBlocking();
            }

            @Override
            public ReactiveVentCollection asReactive() {
                return EmbeddedReactiveVentCollection.this;
            }
        };
    }
}
