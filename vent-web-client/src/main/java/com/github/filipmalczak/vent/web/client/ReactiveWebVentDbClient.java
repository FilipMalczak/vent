package com.github.filipmalczak.vent.web.client;

import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.web.client.temporal.NaiveWebTemporalService;
import com.github.filipmalczak.vent.web.integration.Converters;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.github.filipmalczak.vent.web.paths.CommonPaths.COLLECTION;
import static com.github.filipmalczak.vent.web.paths.CommonPaths.COLLECTIONS;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@EqualsAndHashCode(of = {"webClient"})
@ToString(of = {"webClient"})
public class ReactiveWebVentDbClient implements ReactiveVentDb {
    private WebClient webClient;
    private Converters converters;

    @Getter(lazy = true) private final NaiveWebTemporalService temporalService = new NaiveWebTemporalService(webClient);

    @Override
    public ReactiveVentCollection getCollection(String collectionName) {
        return new WebBasedCollection(collectionName, webClient, converters);
    }

    @Override
    public Flux<String> getManagedCollections() {
        return webClient.get().uri(COLLECTIONS).retrieve().bodyToFlux(String.class);
    }

    @Override
    public Mono<Boolean> isManaged(String collectionName) {
        return webClient.get().uri(COLLECTION, collectionName).exchange().
            map(ClientResponse::statusCode).
            map(HttpStatus::is2xxSuccessful); //todo tighter contract - 204 or 404
    }

    @Override
    public Mono<Success> manage(String collectionName) {
        return webClient.put().uri(COLLECTION, collectionName).retrieve().bodyToMono(Success.class);
    }
}
