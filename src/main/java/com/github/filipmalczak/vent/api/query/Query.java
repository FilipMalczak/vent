package com.github.filipmalczak.vent.api.query;

import com.github.filipmalczak.vent.api.ObjectSnapshot;
import com.github.filipmalczak.vent.api.query.operator.Operator;
import com.github.filipmalczak.vent.embedded.model.Page;
import com.github.filipmalczak.vent.embedded.service.SnapshotRenderer;
import com.github.filipmalczak.vent.embedded.service.SnapshotService;
import com.github.filipmalczak.vent.embedded.service.TemporalService;
import lombok.*;
import org.bson.Document;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.github.filipmalczak.vent.helper.Struct.list;
import static com.github.filipmalczak.vent.helper.Struct.map;
import static com.github.filipmalczak.vent.helper.Struct.pair;
import static java.util.stream.Collectors.toList;

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
 * todo should this be API? operators are quite implementation-specific ; probably provide factory method in DB/collection and abstract Query from impl
 * todo whole thing can probably be done with some smart MongoDB query, but for MVP lets use this approach
 */
@AllArgsConstructor
public class Query {
    private @NonNull String collectionName;
    private @NonNull Operator rootOperator;
    private @NonNull TemporalService temporalService;
    private @NonNull ReactiveMongoTemplate mongoTemplate;
    private @NonNull SnapshotService snapshotService;

    public Flux<ObjectSnapshot> execute(LocalDateTime queryAt){
        long timestamp = temporalService.toTimestamp(queryAt);
        Map<String, Object> candidatePagesMongoQuery = map(
            pair("$and", list(
                pair("startingFrom", pair("$lte", queryAt)),
                pair("$or", list(
                    pair("nextPageFrom", null),
                    pair("nextPageFrom", pair("$gt", queryAt))
                ))
            ))
        );
        return Flux.from(
                mongoTemplate.
                    getCollection(collectionName).
                    find(new Document(candidatePagesMongoQuery), Page.class)
            ).
            filter(p -> p.describesStateAt(queryAt)).
            map(p -> snapshotService.render(p, queryAt)).
            filter(rootOperator.toRuntimeCriteria());
    }
}
