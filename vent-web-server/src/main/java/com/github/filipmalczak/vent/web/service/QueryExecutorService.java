package com.github.filipmalczak.vent.web.service;

import com.github.filipmalczak.vent.api.general.query.CriteriaBuilder;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveQueryBuilder;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveVentQuery;
import com.github.filipmalczak.vent.web.model.query.QueryNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QueryExecutorService {
    @Autowired
    private ReactiveVentDb reactiveVentDb;

    private void applyNodesToBuilder(QueryNode rootNode, CriteriaBuilder builder){
        List<QueryNode> children = rootNode.getChildren();
        List payload = rootNode.getPayload();
        switch (rootNode.getNodeType()){
            case ROOT: applyNodesToBuilder(children, builder); break;
            case AND: builder.and(subbuilder -> applyNodesToBuilder(children, subbuilder)); break;
            case OR: builder.or(subbuilder -> applyNodesToBuilder(children, subbuilder)); break;
            case NOT: builder.not(subbuilder -> applyNodesToBuilder(children, subbuilder)); break;
            //fixme: its ugly to use untyped list as payload
            case EQUALS: builder.equals((String) payload.get(0), payload.get(1)); break;
            //fixme
            default: throw new RuntimeException();
        }
    }

    private void applyNodesToBuilder(List<QueryNode> nodes, CriteriaBuilder builder){
        nodes.stream().forEach(child -> applyNodesToBuilder(child, builder));
    }

    private ReactiveVentQuery prepareQuery(String collectionName, QueryNode rootNode){
        ReactiveQueryBuilder<?, ? extends ReactiveVentQuery> builder = reactiveVentDb.
            getCollection(collectionName).
            queryBuilder();
        applyNodesToBuilder(rootNode, builder);
        return builder.build();
    }

    private LocalDateTime effective(Optional<LocalDateTime> queryAt){
        return queryAt.orElseGet(reactiveVentDb.getTemporalService()::now);
    }

    public Mono<Long> count(String collectionName, QueryNode rootNode, Optional<LocalDateTime> queryAt) {
        return prepareQuery(collectionName, rootNode).count(effective(queryAt));
    }

    public Flux<ObjectSnapshot> find(String collectionName, QueryNode rootNode, Optional<LocalDateTime> queryAt) {
        return prepareQuery(collectionName, rootNode).find(effective(queryAt));
    }

    public Mono<Boolean> exists(String collectionName, QueryNode rootNode, Optional<LocalDateTime> queryAt) {
        return prepareQuery(collectionName, rootNode).exists(effective(queryAt));
    }
}
