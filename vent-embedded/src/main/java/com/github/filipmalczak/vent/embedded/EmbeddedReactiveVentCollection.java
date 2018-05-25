package com.github.filipmalczak.vent.embedded;

import com.github.filipmalczak.vent.api.model.EventConfirmation;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveQueryBuilder;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveVentQuery;
import com.github.filipmalczak.vent.embedded.model.Page;
import com.github.filipmalczak.vent.embedded.model.events.Event;
import com.github.filipmalczak.vent.embedded.model.events.impl.EventFactory;
import com.github.filipmalczak.vent.embedded.query.AndCriteriaBuilder;
import com.github.filipmalczak.vent.embedded.service.MongoQueryPreparator;
import com.github.filipmalczak.vent.embedded.service.PageService;
import com.github.filipmalczak.vent.embedded.service.SnapshotService;
import com.github.filipmalczak.vent.embedded.utils.MongoTranslator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
//todo builder?
public class EmbeddedReactiveVentCollection implements ReactiveVentCollection {
    @Getter private @NonNull String name;

    private @NonNull PageService pageService;

    private @NonNull EventFactory eventFactory;

    private @NonNull SnapshotService snapshotService;

    private @NonNull MongoQueryPreparator mongoQueryPreparator;

    private @NonNull ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Success> drop() {
        //fixme this is ugly, it shouldnt be pageServices responsibility, but I don't want dependency on template
        return pageService.drop(name);
    }

    @Override
    public Mono<VentId> create(Map initialState) {
        return pageService.
            createFirstPage(name, initialState).
            map(Page::getObjectId).
            map(MongoTranslator::fromMongo);
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
    public Mono<ObjectSnapshot> get(VentId id, LocalDateTime queryAt) {
        return snapshotService.getSnapshot(name, id, queryAt);
    }

    @Override
    public Flux<VentId> identifyAll(LocalDateTime queryAt) {
        return pageService.allPages(name, queryAt).map(Page::getObjectId).map(MongoTranslator::fromMongo);
    }

    @Override
    public Mono<EventConfirmation> update(VentId id, Map newState) {
        //todo analyse impact to criteria for Equals, see com.github.filipmalczak.vent.api.query.operator.EqualsOperator
        //todo check whether query can be optimized with this approach
        //todo once crowding is implemented, test this properly
        return addEventToNewPage(id, eventFactory.update(newState));
    }

    @Override
    public ReactiveQueryBuilder<?, ? extends ReactiveVentQuery> queryBuilder() {
        return new EmbeddedReactiveQueryBuilder(name, new AndCriteriaBuilder(), mongoQueryPreparator, mongoTemplate, snapshotService);
    }

    private Mono<EventConfirmation> addEventToNewPage(VentId id, Event event){
        return pageService.
            createEmptyNextPage(name, id).
            flatMap(p -> pageService.addEvent(name, p, event));
    }

    private Mono<EventConfirmation> addEventToCurrentPage(VentId id, Event event){
        return pageService.
            currentPage(name, id).
            flatMap(p -> pageService.addEvent(name, p, event));
    }
}
