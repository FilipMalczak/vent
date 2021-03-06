package com.github.filipmalczak.vent.mongo.service;

import com.github.filipmalczak.vent.api.model.EventConfirmation;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.api.temporal.TemporallyEnabled;
import com.github.filipmalczak.vent.mongo.model.Page;
import com.github.filipmalczak.vent.mongo.model.events.Event;
import com.github.filipmalczak.vent.mongo.model.events.impl.Delete;
import com.github.filipmalczak.vent.mongo.model.events.impl.EventFactory;
import com.github.filipmalczak.vent.mongo.utils.MongoTranslator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;

import static com.github.filipmalczak.vent.mongo.utils.MongoTranslator.toMongo;
import static java.util.Arrays.asList;
import static reactor.core.publisher.Mono.just;

@AllArgsConstructor
public class PageService implements TemporallyEnabled {
    @Getter private @NonNull TemporalService temporalService;

    private @NonNull
    ReactiveMongoOperations mongoOperations;

    private @NonNull EventFactory eventFactory;

    public Mono<Page> createFirstPage(@NonNull String collectionName, @NonNull LocalDateTime now, @NonNull Map initialState){
        Event create = eventFactory.create(initialState).withOccuredOn(now);
        Page page = new Page(
            null,
            new ObjectId(temporalService.toDate(now)),
            null,
            null,
            -1L, //CREATE should be version #0
            now,
            null,
            null,
            asList(create),
            null
        );
        return mongoOperations.insert(page, collectionName);
    }

    private Flux<Page> query(String collectionName, Query query){
        return mongoOperations.find(
            query,
            Page.class,
            collectionName
        );
    }

    private Flux<Page> query(String collectionName, Criteria criteria){
        return query(collectionName, Query.query(criteria));
    }

    /**
     * Actually all undeleted pages. If this distinction will become needed, I'll refactor.
     */
    public Flux<Page> allPages(@NonNull String collectionName, @NonNull LocalDateTime at){
        return query(
            collectionName,
            Criteria.where(null).orOperator(
                Criteria.where("objectDeletedOn").gt(at),
                Criteria.where("objectDeletedOn").is(null)
            )
        );
    }

    public Flux<Page> allPages(@NonNull String collectionName, @NonNull VentId id){
        return query(
            collectionName,
            Criteria.where("objectId").is(toMongo(id))
        );
    }

    public Mono<Page> pageAtTimestamp(@NonNull String collectionName, @NonNull VentId id, @NonNull LocalDateTime at){
        return query(
                collectionName,
                Criteria.where("objectId").is(toMongo(id)).
                    and("startingFrom").lte(at).
                    andOperator(
                        Criteria.where("").orOperator(
                            Criteria.where("nextPageFrom").gt(at),
                            Criteria.where("nextPageFrom").is(null)
                        ),
                        Criteria.where("").orOperator(
                            Criteria.where("objectDeletedOn").gt(at),
                            Criteria.where("objectDeletedOn").is(null)
                        )
                    )
            ).
            sort(Comparator.comparing(Page::getStartingFrom)).
            next();
    }

    public Mono<Page> createEmptyNextPage(@NonNull String collectionName, @NonNull VentId id, @NonNull LocalDateTime startingFrom){
        return currentPage(collectionName, id).flatMap(p -> createEmptyNextPage(collectionName, p, startingFrom));
    }

    public Mono<Page> createEmptyNextPage(@NonNull String collectionName, @NonNull Page previousPage, @NonNull LocalDateTime startingFrom){
        Page newPage = new Page(
            null,
            previousPage.getObjectId(),
            previousPage.getPageId(),
            null,
            previousPage.getFromVersion() + previousPage.getEvents().size(),
            startingFrom,
            null,
            null,
            new LinkedList<>(),
            null
        );
        return mongoOperations.
            insert(newPage, collectionName).
            flatMap(p -> {
                previousPage.setNextPageFrom(p.getStartingFrom());
                previousPage.setNextPageId(p.getPageId());
                return mongoOperations.
                    save(previousPage, collectionName).
                    then(just(p));
            });
    }

    public Mono<Page> currentPage(@NonNull String collectionName, @NonNull VentId id){
        return query(
            collectionName,
            Criteria.where("objectId").is(toMongo(id)).
                and("nextPageFrom").is(null)
        ).next();
    }

    public Mono<EventConfirmation> addEvent(@NonNull String collectionName, @NonNull Page page, @NonNull Event event){
        EventConfirmation confirmation = page.addEvent(event);
        return mongoOperations.save(page, collectionName).then(just(confirmation));
    }

    public Mono<EventConfirmation> delete(@NonNull String collectionName, @NonNull LocalDateTime now, @NonNull Page page){
        Delete delete = eventFactory.delete().withOccuredOn(now);
        EventConfirmation confirmation = page.addEvent(delete);
        page.setObjectDeletedOn(now);
        return mongoOperations.save(page, collectionName).then(just(confirmation));
    }

    //fixme doesnt fit the responsibility, see VentCollection.drop
    public Mono<Success> drop(String collectionName){
        return Mono.from(mongoOperations.getCollection(collectionName).drop()).map(MongoTranslator::fromMongo);
    }
}
