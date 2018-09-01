package com.github.filipmalczak.vent.web.client;

import com.github.filipmalczak.vent.api.model.EventConfirmation;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.web.client.query.WebCriteriaBuilder;
import com.github.filipmalczak.vent.web.client.query.WebQuery;
import com.github.filipmalczak.vent.web.client.query.WebQueryBuilder;
import com.github.filipmalczak.vent.web.client.temporal.NaiveWebTemporalService;
import com.github.filipmalczak.vent.web.integration.Converters;
import com.github.filipmalczak.vent.web.integration.DateFormat;
import com.github.filipmalczak.vent.web.model.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Supplier;

import static com.github.filipmalczak.vent.web.paths.CommonPaths.*;
import static reactor.core.publisher.Mono.just;

@AllArgsConstructor
@EqualsAndHashCode(of = {"ventCollectionName", "webClient"})
@ToString(of = {"ventCollectionName", "webClient"})
public class WebBasedCollection implements ReactiveVentCollection<WebQueryBuilder, WebQuery> {
    @Getter private String ventCollectionName;
    private WebClient webClient;
    private Converters converters;

    @Getter(lazy = true) private final NaiveWebTemporalService temporalService = new NaiveWebTemporalService(webClient);

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
    public Flux<VentId> identifyAll(Supplier<LocalDateTime> queryAt) {
        return webClient.get().
            uri(IDS_WITH_QUERY_TIME, ventCollectionName, queryAt.get()).
            retrieve().
            bodyToFlux(IdView.class).
            map(converters::convert);
    }

    @Override
    public Mono<ObjectSnapshot> get(VentId id, Supplier<LocalDateTime> queryAt) {
        return webClient.get().
            uri(OBJECT_WITH_QUERY_TIME, ventCollectionName, id.getValue(), DateFormat.QUERY_AT.format(queryAt.get())).
            retrieve().
            bodyToMono(ObjectView.class).
            map(converters::convert);
    }

    @Override
    public WebQueryBuilder queryBuilder() {
        return new WebQueryBuilder(webClient, ventCollectionName, new WebCriteriaBuilder(), converters);
    }

    @Override
    public Flux<ObjectSnapshot> getAll(Supplier<LocalDateTime> queryAt) {
        return webClient.get().
            uri(OBJECTS_WITH_QUERY_TIME, ventCollectionName, DateFormat.QUERY_AT.format(queryAt.get())).
            retrieve().
            bodyToFlux(ObjectView.class).
            map(converters::convert);
    }
}
