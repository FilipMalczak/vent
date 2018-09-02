package com.github.filipmalczak.ventrello.impl;

import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.github.filipmalczak.vent.helper.Struct.map;
import static com.github.filipmalczak.vent.helper.Struct.pair;
import static reactor.core.publisher.Mono.empty;
import static reactor.core.publisher.Mono.fromRunnable;
import static reactor.core.publisher.Mono.just;

@Profile("mockData")
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class InitializeMockData {
    private final ReactiveVentDb<?, ?, ?> reactiveVentDb;
    private final CountersManager countersManager;

    public Mono<Success> initialize(){
        return reactiveVentDb.getCollection("tasks").identifyAll().hasElements().
            flatMap(b ->
                b ?
                    just(Success.NO_OP_SUCCESS) :
                    createTask("First task", "Stuff to be done").
                        then(createTask("Another task", "More stuff to be done")).
                        then(just(Success.SUCCESS))
                ).
                flatMapMany(x -> reactiveVentDb.getCollection("tasks").getAll()).
                map(o -> {log.info("Task in DB: {}", o); return o;}).
                then(just(Success.SUCCESS));
    }

    private Mono<?> createTask(String name, String description){
        return createTask(name, description, false);
    }

    private Mono<?> createTask(String name, String description, boolean resolved){
        return countersManager.increment("tasks").
            map(i -> map(
                pair("number", i),
                pair("name", name),
                pair("description", description),
                pair("resolved", resolved)
            )).
            flatMap(b ->
            reactiveVentDb.
                getCollection("tasks").
                create(b).flatMap(id -> reactiveVentDb.getCollection("tasks").get(id))
        );
    }
}
