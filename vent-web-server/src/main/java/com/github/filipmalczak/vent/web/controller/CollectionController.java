package com.github.filipmalczak.vent.web.controller;

import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.web.integration.Converters;
import com.github.filipmalczak.vent.web.model.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static com.github.filipmalczak.vent.helper.Struct.pair;
import static com.github.filipmalczak.vent.web.paths.CommonPaths.*;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class CollectionController {
    private ReactiveVentDb<?, ?, ?> reactiveVentDb;
    private Converters converters;

    @DeleteMapping(COLLECTION)
    public Mono<Success> drop(@PathVariable String name){
        return reactiveVentDb.getCollection(name).drop();
    }

    @PostMapping(OBJECTS)
    public Mono<IdView> create(@PathVariable String name, @RequestBody CreateRequest request){
        return reactiveVentDb.getCollection(name).
            create(request.getInitialState()).
            map(converters::convert);
    }

    @PutMapping(STATE)
    public Mono<EventConfirmationView> putValue(@PathVariable String name, @PathVariable String id,
                                                @RequestParam String path, @RequestBody NewStateRequest request){
        return reactiveVentDb.getCollection(name).
            putValue(new VentId(id), path, request.getNewState()).
            map(converters::convert);
    }

    @DeleteMapping(STATE)
    public Mono<EventConfirmationView> deleteValue(@PathVariable String name, @PathVariable String id,
                                                    @RequestParam String path){
        return reactiveVentDb.getCollection(name).
            deleteValue(new VentId(id), path).
            map(converters::convert);
    }

    @PutMapping(OBJECT)
    public Mono<EventConfirmationView> update(@PathVariable String name, @PathVariable String id,
                                                @RequestBody NewStateRequest request){
        Map updatePayload = request.getNewState() instanceof Map ?
            (Map) request.getNewState() :
            pair("value", request.getNewState());
        return reactiveVentDb.getCollection(name).
            update(new VentId(id), updatePayload).
            map(converters::convert);
    }

    @DeleteMapping(OBJECT)
    public Mono<EventConfirmationView> update(@PathVariable String name, @PathVariable String id){
        return reactiveVentDb.getCollection(name).
            delete(new VentId(id)).
            map(converters::convert);
    }

    @GetMapping(IDS)
    public Flux<IdView> identifyAll(@PathVariable String name, @RequestParam LocalDateTime queryAt){
        return reactiveVentDb.getCollection(name).
            identifyAll(queryAt).
            map(converters::convert);
    }

    @GetMapping(OBJECT)
    //todo optional queryAt, same with most of read stack
    public Mono<ObjectView> get(@PathVariable String name, @PathVariable String id, @RequestParam LocalDateTime queryAt){
        return reactiveVentDb.getCollection(name).
            get(new VentId(id), queryAt).
            map(converters::convert);
    }

    @GetMapping(OBJECTS)
    public Flux<ObjectView> getAll(@PathVariable String name, @RequestParam(required = false) Optional<LocalDateTime> queryAt){
        return reactiveVentDb.getCollection(name).
                    getAll(queryAt.orElse(reactiveVentDb.getTemporalService().now())).
                    map(converters::convert);
    }
}
