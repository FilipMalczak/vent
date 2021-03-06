package com.github.filipmalczak.vent.web.client.query;

import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveVentQuery;
import com.github.filipmalczak.vent.web.client.temporal.NaiveWebTemporalService;
import com.github.filipmalczak.vent.web.integration.Converters;
import com.github.filipmalczak.vent.web.model.ObjectView;
import com.github.filipmalczak.vent.web.model.query.ExecuteQueryRequest;
import com.github.filipmalczak.vent.web.model.query.Operation;
import com.github.filipmalczak.vent.web.model.query.QueryNode;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import static com.github.filipmalczak.vent.web.paths.CommonPaths.QUERY_WITH_TIME;
import static reactor.core.publisher.Mono.just;

@AllArgsConstructor
@ToString(exclude = {"webClient", "converters"})
@EqualsAndHashCode
public class WebQuery implements ReactiveVentQuery {
    private WebClient webClient;
    private String ventCollectionName;
    private QueryNode rootNode;
    private Converters converters;

    @Getter(lazy = true) private final NaiveWebTemporalService temporalService = new NaiveWebTemporalService(webClient);

    private Mono<ClientResponse> exchange(Operation operation, LocalDateTime queryAt){
         return webClient.post().
            uri(QUERY_WITH_TIME, ventCollectionName, queryAt).
            body(just(new ExecuteQueryRequest(operation, rootNode)), ExecuteQueryRequest.class).
            exchange().log("EXCHANGED");
    }

    @Override
    public Flux<ObjectSnapshot> find(Supplier<LocalDateTime> queryAt) {
        return exchange(Operation.FIND, queryAt.get()).
            flux().
            flatMap(clientResponse -> clientResponse.bodyToFlux(ObjectView.class)).map(converters::convert).log("FIND");
    }

    @Override
    public Mono<Long> count(Supplier<LocalDateTime> queryAt) {
        return exchange(Operation.COUNT, queryAt.get()).
            flatMap(clientResponse -> clientResponse.bodyToMono(Long.class));
    }

    @Override
    public Mono<Boolean> exists(Supplier<LocalDateTime> queryAt) {
        return exchange(Operation.EXISTS, queryAt.get()).
            flatMap(clientResponse -> clientResponse.bodyToMono(Boolean.class));
    }
}
