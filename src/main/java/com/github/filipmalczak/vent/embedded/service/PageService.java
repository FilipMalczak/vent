package com.github.filipmalczak.vent.embedded.service;

import com.github.filipmalczak.vent.api.EventConfirmation;
import com.github.filipmalczak.vent.api.Success;
import com.github.filipmalczak.vent.api.VentId;
import com.github.filipmalczak.vent.embedded.model.Page;
import com.github.filipmalczak.vent.embedded.model.events.Event;
import com.github.filipmalczak.vent.embedded.model.events.impl.EventFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;

import static java.util.Arrays.asList;
import static reactor.core.publisher.Mono.just;

@Service
@Getter @Setter @AllArgsConstructor
public class PageService {
    @Autowired
    private @NonNull TemporalService temporalService;

    @Autowired
    private @NonNull ReactiveMongoTemplate mongoTemplate;

    @Autowired
    private @NonNull EventFactory eventFactory;

    public Mono<Page> createFirstPage(@NonNull String collectionName, @NonNull Map initialState){
        Event create = eventFactory.create(initialState);
        LocalDateTime now = create.getOccuredOn();
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
        return mongoTemplate.insert(page, collectionName);
    }

    private Flux<Page> query(String collectionName, Query query){
        return mongoTemplate.find(
            query,
            Page.class,
            collectionName
        );
    }

    private Flux<Page> query(String collectionName, Criteria criteria){
        return query(collectionName, Query.query(criteria));
    }

    /**
     * Actually all undeleted pages. If this will become needed, I'll refactor.
     */
    public Flux<Page> allPages(@NonNull String collectionName, @NonNull LocalDateTime at){
        return query(
            collectionName,
            Criteria.where(null).orOperator(
                Criteria.where("finishedOn").gt(at),
                Criteria.where("finishedOn").is(null)
            )
        );
    }

    public Flux<Page> allPages(@NonNull String collectionName, @NonNull VentId id){
        return query(
            collectionName,
            Criteria.where("objectId").is(id.toMongoId())
        );
    }

    public Mono<Page> pageAtTimestamp(@NonNull String collectionName, @NonNull VentId id, @NonNull LocalDateTime at){
        return query(
                collectionName,
                Criteria.where("objectId").is(id.toMongoId()).
                    and("startingFrom").lte(at).
                    orOperator(
                        Criteria.where("nextPageFrom").gt(at),
                        Criteria.where("nextPageFrom").is(null)
                    )
            ).
            sort(Comparator.comparing(Page::getStartingFrom)).
            next();
    }

    public Mono<Page> currentPage(@NonNull String collectionName, @NonNull VentId id){
        return query(
            collectionName,
            Criteria.where("objectId").is(id.toMongoId()).
                and("nextPageFrom").is(null)
        ).next();
    }

    public Mono<EventConfirmation> addEvent(@NonNull String collectionName, @NonNull Page page, @NonNull Event event){
        EventConfirmation confirmation = page.addEvent(event);
        return mongoTemplate.save(page, collectionName).then(just(confirmation));

    }

    //fixme doesnt fit the responsibility, see EmbeddedReactiveVentCollection.drop
    public Mono<Success> drop(String collectionName){
        return Mono.from(mongoTemplate.getCollection(collectionName).drop()).map(Success::fromMongoSuccess);
    }
}
