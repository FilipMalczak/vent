package com.github.filipmalczak.vent.web.controller;

import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.github.filipmalczak.vent.web.paths.CommonPaths.COLLECTION;
import static com.github.filipmalczak.vent.web.paths.CommonPaths.COLLECTIONS;

//todo this can be done functional style, without annotations
// https://docs.spring.io/spring/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/html/web-reactive.html#_routerfunctions
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class DbController {
    private ReactiveVentDb<?, ?, ?> reactiveVentDb;

    @GetMapping(COLLECTIONS)
    public Flux<String> getManagedCollections(){
        return reactiveVentDb.getManagedCollections().log("collections");
    }

    @GetMapping(COLLECTION)
    public Mono<ServerResponse> isManaged(@PathVariable String name){
        return reactiveVentDb.isManaged(name).
            filter(b->b).
            flatMap(yup -> ServerResponse.status(HttpStatus.NO_CONTENT).build()).
            switchIfEmpty(ServerResponse.notFound().build());
    }

    @PutMapping(COLLECTION)
    public Mono<Success> manage(@PathVariable String name){
        return reactiveVentDb.manage(name).log("manage");
    }
}
