package com.github.filipmalczak.vent.web.client;

import com.github.filipmalczak.vent.api.model.EventConfirmation;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveQueryBuilder;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveVentQuery;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.web.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ma.glasnost.orika.MapperFacade;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

import static com.github.filipmalczak.vent.web.paths.CommonPaths.*;
import static reactor.core.publisher.Mono.just;

@AllArgsConstructor
public class WebBasedCollection implements ReactiveVentCollection {
    @Getter private String name;
    private WebClient webClient;
    private MapperFacade mapperFacade;

    @Override
    public Mono<Success> drop() {
        return webClient.delete().uri(COLLECTION, name).retrieve().bodyToMono(Success.class);
    }

    @Override
    public Mono<VentId> create(Map initialState) {
        return webClient.post().
            uri(OBJECTS, name).
            body(just(new CreateRequest(initialState)), CreateRequest.class).
            retrieve().
            bodyToMono(IdView.class).
            map(view -> mapperFacade.map(view, VentId.class));
    }

    @Override
    public Mono<EventConfirmation> putValue(VentId id, String path, Object value) {
        return webClient.put().
            uri(STATE_WITH_PATH, name, id.getValue(), path).
            body(just(new NewStateRequest(value)), NewStateRequest.class).
            retrieve().
            bodyToMono(EventConfirmationView.class).
            map(view -> mapperFacade.map(view, EventConfirmation.class));
    }

    @Override
    public Mono<EventConfirmation> deleteValue(VentId id, String path) {
        return webClient.delete().
            uri(STATE_WITH_PATH, name, id.getValue(), path).
            retrieve().
            bodyToMono(EventConfirmationView.class).
            map(view -> mapperFacade.map(view, EventConfirmation.class));
    }

    @Override
    public Mono<EventConfirmation> update(VentId id, Map newState) {
        return webClient.put().
            uri(OBJECT, name, id.getValue()).
            body(just(new NewStateRequest(newState)), NewStateRequest.class).
            retrieve().
            bodyToMono(EventConfirmationView.class).
            map(view -> mapperFacade.map(view, EventConfirmation.class));
    }

    @Override
    public Mono<EventConfirmation> delete(VentId id) {
        return webClient.delete().
            uri(OBJECT, name, id.getValue()).
            retrieve().
            bodyToMono(EventConfirmationView.class).
            map(view -> mapperFacade.map(view, EventConfirmation.class));
    }

    @Override
    public Flux<VentId> identifyAll(LocalDateTime queryAt) {
        return webClient.head().
            uri(OBJECTS_WITH_QUERY_TIME, name, queryAt).
            retrieve().
            bodyToFlux(IdView.class).
            map(view -> mapperFacade.map(view, VentId.class));
    }

    @Override
    public Mono<ObjectSnapshot> get(VentId id, LocalDateTime queryAt) {
        return webClient.get().
            uri(OBJECT_WITH_QUERY_TIME, name, id.getValue(), queryAt).
            retrieve().
            bodyToMono(ObjectView.class).
            map(view -> mapperFacade.map(view, ObjectSnapshot.class));
    }

    //todo
    @Override
    public ReactiveQueryBuilder<?, ? extends ReactiveVentQuery> queryBuilder() {
        return null;
    }

    @Override
    public Flux<ObjectSnapshot> getAll(LocalDateTime queryAt) {
        return webClient.get().
            uri(OBJECTS_WITH_QUERY_TIME, name, queryAt).
            retrieve().
            bodyToFlux(ObjectView.class).
            map(view -> mapperFacade.map(view, ObjectSnapshot.class));
    }

    @Override
    public TemporalService getTemporalService() {
        return null;//todo delegating over HTTP
    }
}
