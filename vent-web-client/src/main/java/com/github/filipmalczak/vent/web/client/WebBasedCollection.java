package com.github.filipmalczak.vent.web.client;

import com.github.filipmalczak.vent.api.model.EventConfirmation;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveQueryBuilder;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveVentQuery;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.web.integration.Converters;
import com.github.filipmalczak.vent.web.integration.DateFormat;
import com.github.filipmalczak.vent.web.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

import static com.github.filipmalczak.vent.web.paths.CommonPaths.*;
import static reactor.core.publisher.Mono.just;

@AllArgsConstructor
public class WebBasedCollection implements ReactiveVentCollection {
    @Getter private String ventCollectionName;
    private WebClient webClient;
    private Converters converters;

    @Override
    public Mono<Success> drop() {
        return webClient.delete().uri(COLLECTION, ventCollectionName).retrieve().bodyToMono(Success.class);
    }

    @Override
    public Mono<VentId> create(Map initialState) {
        return webClient.post().
            uri(OBJECTS, ventCollectionName).
            body(just(new CreateRequest(initialState)), CreateRequest.class).
            retrieve().
            bodyToMono(IdView.class).
            map(converters::convert);
    }

    @Override
    public Mono<EventConfirmation> putValue(VentId id, String path, Object value) {
        return webClient.put().
            uri(STATE_WITH_PATH, ventCollectionName, id.getValue(), path).
            body(just(new NewStateRequest(value)), NewStateRequest.class).
            retrieve().
            bodyToMono(EventConfirmationView.class).
            map(converters::convert);
    }

    @Override
    public Mono<EventConfirmation> deleteValue(VentId id, String path) {
        return webClient.delete().
            uri(STATE_WITH_PATH, ventCollectionName, id.getValue(), path).
            retrieve().
            bodyToMono(EventConfirmationView.class).
            map(converters::convert);
    }

    @Override
    public Mono<EventConfirmation> update(VentId id, Map newState) {
        return webClient.put().
            uri(OBJECT, ventCollectionName, id.getValue()).
            body(just(new NewStateRequest(newState)), NewStateRequest.class).
            retrieve().
            bodyToMono(EventConfirmationView.class).
            map(converters::convert);
    }

    @Override
    public Mono<EventConfirmation> delete(VentId id) {
        return webClient.delete().
            uri(OBJECT, ventCollectionName, id.getValue()).
            retrieve().
            bodyToMono(EventConfirmationView.class).
            map(converters::convert);
    }

    @Override
    public Flux<VentId> identifyAll(LocalDateTime queryAt) {
        return webClient.get().
            uri(IDS_WITH_QUERY_TIME, ventCollectionName, queryAt).
            retrieve().
            bodyToFlux(IdView.class).
            map(converters::convert);
    }

    @Override
    public Mono<ObjectSnapshot> get(VentId id, LocalDateTime queryAt) {
        return webClient.get().
            uri(OBJECT_WITH_QUERY_TIME, ventCollectionName, id.getValue(), DateFormat.QUERY_AT.format(queryAt)).
            retrieve().
            bodyToMono(ObjectView.class).
            map(converters::convert);
    }

    //todo
    @Override
    public WebQueryBuilder queryBuilder() {
        return new WebQueryBuilder(webClient, ventCollectionName, new WebCriteriaBuilder());
    }

    @Override
    public Flux<ObjectSnapshot> getAll(LocalDateTime queryAt) {
        return webClient.get().
            uri(OBJECTS_WITH_QUERY_TIME, ventCollectionName, DateFormat.QUERY_AT.format(queryAt)).
            retrieve().
            bodyToFlux(ObjectView.class).
            map(converters::convert);
    }

    @Override
    public TemporalService getTemporalService() {
        return null;//todo delegating over HTTP
    }
}
