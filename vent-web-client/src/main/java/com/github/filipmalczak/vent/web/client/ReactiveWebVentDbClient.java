package com.github.filipmalczak.vent.web.client;

import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.github.filipmalczak.vent.web.paths.CommonPaths.COLLECTION;
import static com.github.filipmalczak.vent.web.paths.CommonPaths.COLLECTIONS;
import static com.github.filipmalczak.vent.web.paths.CommonPaths.OPTIMIZE;

@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ReactiveWebVentDbClient implements ReactiveVentDb {
    private WebClient webClient;
    private MapperFacade mapperFacade;

    @Override
    public ReactiveVentCollection getCollection(String collectionName) {
        return new WebBasedCollection(collectionName, webClient, mapperFacade);
    }

    @Override
    public Mono<Success> optimizePages(SuggestionStrength strength, OptimizationType type) {
        return webClient.post().uri(OPTIMIZE, strength, type).retrieve().bodyToMono(Success.class);
    }

    @Override
    public Flux<String> getManagedCollections() {
        return webClient.head().uri(COLLECTIONS).retrieve().bodyToFlux(String.class);
    }

    @Override
    public Mono<Boolean> isManaged(String collectionName) {
        return webClient.head().uri(COLLECTION, collectionName).exchange().
            map(r -> r.statusCode()).
            map(c -> c.is2xxSuccessful()); //todo tighter contract - 204 or 404
    }

    @Override
    public Mono<Success> manage(String collectionName) {
        return webClient.put().uri(COLLECTION, collectionName).retrieve().bodyToMono(Success.class);
    }

    @Override
    public TemporalService getTemporalService() {
        return null; //todo
    }
}
