package com.github.filipmalczak.vent.embedded.query;

import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveVentQuery;
import com.github.filipmalczak.vent.embedded.model.Page;
import com.github.filipmalczak.vent.embedded.model.events.impl.Create;
import com.github.filipmalczak.vent.embedded.query.operator.Operator;
import com.github.filipmalczak.vent.embedded.service.MongoQueryPreparator;
import com.github.filipmalczak.vent.embedded.service.SnapshotService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Map;

import static com.github.filipmalczak.vent.helper.Struct.*;

/**
 * Querying happens in 2 steps:
 *
 * First, we query MongoDB for all pages that match required time period (done on Query level) and have events that
 * hint that the query can be satisfied (e.g. if we ask "path x.y.z equals to 1" then we search for pages with PutValue
 * events that occured before query time with path x.y.z and value 1 or some superpath and map/object value).
 * The query for this step is obtained from toMongoEventCriteria and toMongoInitialStateCriteria methods of Operator.
 *
 * Second step is creating snapshots for all candidate pages and then filtering them out in runtime. This happens with
 * simple Predicate obtained from Operator by toRuntimeCriteria.
 *
 * todo whole thing can probably be done with some smart MongoDB query, but for MVP lets use this approach
 */
@AllArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode
public class EmbeddedReactiveQuery implements ReactiveVentQuery{
    private @NonNull String collectionName;
    private @NonNull Operator rootOperator;
    private @NonNull MongoQueryPreparator mongoQueryPreparator;
    private @NonNull ReactiveMongoTemplate mongoTemplate;
    private @NonNull SnapshotService snapshotService;

    @SneakyThrows
    public Flux<ObjectSnapshot> find(LocalDateTime queryAt){
        Map<String, Object> candidatePagesMongoQuery = map(
            pair("startingFrom", pair("$lte", queryAt)),
            pair("$or", list(
                pair("nextPageFrom", null),
                pair("nextPageFrom", pair("$gt", queryAt))
            )),
            pair("$or", list(
                pair("objectDeletedOn", null),
                pair("objectDeletedOn", pair("$gt", queryAt))
            )),
            pair("$or", list(
                pair("events", pair("$elemMatch", rootOperator.toMongoEventCriteria())),
                pair("events.0", map(
                    pair("_class", Create.class.getCanonicalName()),
                    pair("initialState", rootOperator.toMongoInitialStateCriteria())
                )),
                pair("initialState", rootOperator.toMongoInitialStateCriteria())
            ))
        );
        candidatePagesMongoQuery = mongoQueryPreparator.prepare(candidatePagesMongoQuery);
        return mongoTemplate.find(new BasicQuery(new Document(candidatePagesMongoQuery)), Page.class, collectionName).
            //fixme I think that this is redundant
            filter(p -> p.describesStateAt(queryAt)).
            map(p -> snapshotService.render(p, queryAt)).
            filter(x -> rootOperator.toRuntimeCriteria().test(x.getState()));
    }
}
