package com.github.filipmalczak.vent.mongo.service;

import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.mongo.model.CollectionDescriptor;
import com.github.filipmalczak.vent.mongo.model.CollectionPeriodDescriptor;
import com.github.filipmalczak.vent.mongo.model.events.Event;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

import static com.github.filipmalczak.vent.helper.Struct.list;
import static com.github.filipmalczak.vent.mongo.utils.CollectionsUtils.COLLECTIONS_MONGO_COLLECTION;
import static com.github.filipmalczak.vent.mongo.utils.CollectionsUtils.MONGO_COLLECTION_NAME_MAPPER;
import static reactor.core.publisher.Mono.just;

@AllArgsConstructor
public class CollectionService {
    private final ReactiveMongoOperations operations;
    private final TemporalService temporalService;

    @Value(staticConstructor = "with")
    public static class NameAndNow {
        final String name;
        final LocalDateTime now;

        public <E extends Event<E>> E align(E event){
            return event.withOccuredOn(now);
        }
    }

    public Mono<NameAndNow> mongoCollectionName(String ventCollectionName){
        return mongoCollectionName(ventCollectionName, temporalService);
    }
    public Mono<NameAndNow> mongoCollectionName(String ventCollectionName, Supplier<LocalDateTime> at){
        //todo introduce archivization
        //make sure that supplier is called in reactive context (and not outside of some publisher)
        return getMongoCollectionNameForCurrentPeriod(ventCollectionName).map(s -> NameAndNow.with(s, at.get()));
    }

    public Mono<String> getMongoCollectionNameForCurrentPeriod(String ventCollectionName){
        return manageIfNeededAndGet(ventCollectionName).
            map(CollectionDescriptor::getCurrentPeriod).
            map(CollectionPeriodDescriptor::getMongoCollectionName);
    }

    public Flux<CollectionDescriptor> getAllCollections(){
        return operations.findAll(CollectionDescriptor.class, COLLECTIONS_MONGO_COLLECTION);
    }

    public Flux<String> getAllCollectionNames(){
        return getAllCollections().map(CollectionDescriptor::getVentCollectionName);
    }


    //todo maybe return Mono<Yes> (empty on No) instead of Mono<Boolean>?
    public Mono<Boolean> isManaged(String ventCollectionName){
        return operations.
            exists(
                Query.query(
                    Criteria.
                        where("ventCollectionName").
                        is(ventCollectionName)
                ),
                CollectionDescriptor.class,
                COLLECTIONS_MONGO_COLLECTION
            );
    }

    public Mono<CollectionDescriptor> getDescriptor(String ventCollectionName){
        return operations.findOne(
            Query.query(
                Criteria.
                    where("ventCollectionName").
                    is(ventCollectionName)
            ),
            CollectionDescriptor.class,
            COLLECTIONS_MONGO_COLLECTION
        );
    }

    public Mono<CollectionDescriptor> manageIfNeededAndGet(String ventCollectionName){
        return getDescriptor(ventCollectionName).switchIfEmpty(createManaged(ventCollectionName));
    }

    private Mono<CollectionDescriptor> createManaged(String ventCollectionName){
        return Mono.
            fromCallable(temporalService::now).
            flatMap( now ->
                operations.insert(
                    new CollectionDescriptor(
                        ventCollectionName,
                        new CollectionPeriodDescriptor(
                            now,
                            null,
                            MONGO_COLLECTION_NAME_MAPPER.
                                toMongoCollectionName(
                                    ventCollectionName,
                                    now,
                                    Optional.empty()
                                )
                        ),
                        list()
                    ),
                    COLLECTIONS_MONGO_COLLECTION
                )
            );
    }

    public Mono<Success> manage(String ventCollectionName){
        //fixme between isManaged and optionally inserting there's a gap that is perfect for race conditions
        return isManaged(ventCollectionName).flatMap(isMngd ->
            isMngd ?
                just(Success.NO_OP_SUCCESS):
                createManaged(ventCollectionName).map( newCollection ->
                    Success.SUCCESS
                )
        );
    }
}
