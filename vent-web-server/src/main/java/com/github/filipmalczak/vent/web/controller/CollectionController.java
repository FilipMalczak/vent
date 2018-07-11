package com.github.filipmalczak.vent.web.controller;

import com.github.filipmalczak.vent.api.general.VentDb;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.web.model.*;
import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

import static com.github.filipmalczak.vent.web.paths.CommonPaths.*;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class CollectionController {
    private ReactiveVentDb reactiveVentDb;
    private MapperFacade mapperFacade;

    @DeleteMapping(COLLECTION)
    public Mono<Success> drop(@PathVariable String name){
        return reactiveVentDb.getCollection(name).drop();
    }

    @PostMapping(OBJECTS)
    public Mono<IdView> create(@PathVariable String name, @RequestBody CreateRequest request){
        return reactiveVentDb.getCollection(name).
            create(request.getInitialState()).
            map(id -> mapperFacade.map(id, IdView.class));
    }

    @PutMapping(STATE)
    public Mono<EventConfirmationView> putValue(@PathVariable String name, @PathVariable String id,
                                                @RequestParam String path, @RequestBody NewStateRequest request){
        return reactiveVentDb.getCollection(name).
            putValue(new VentId(id), path, request.getNewState()).
            map(confirmation -> mapperFacade.map(confirmation, EventConfirmationView.class));
    }

    @DeleteMapping(STATE)
    public Mono<EventConfirmationView> deleteValue(@PathVariable String name, @PathVariable String id,
                                                    @RequestParam String path){
        return reactiveVentDb.getCollection(name).
            deleteValue(new VentId(id), path).
            map(confirmation -> mapperFacade.map(confirmation, EventConfirmationView.class));
    }

    @PutMapping(OBJECT)
    public Mono<EventConfirmationView> update(@PathVariable String name, @PathVariable String id,
                                                @RequestBody NewStateRequest request){
        //fixme ugly downcast to Map
        return reactiveVentDb.getCollection(name).
            update(new VentId(id), (Map) request.getNewState()).
            map(confirmation -> mapperFacade.map(confirmation, EventConfirmationView.class));
    }

    @DeleteMapping(OBJECT)
    public Mono<EventConfirmationView> update(@PathVariable String name, @PathVariable String id){
        return reactiveVentDb.getCollection(name).
            delete(new VentId(id)).
            map(confirmation -> mapperFacade.map(confirmation, EventConfirmationView.class));
    }

    @RequestMapping(path = OBJECTS, method = RequestMethod.HEAD)
    public Flux<IdView> identifyAll(@PathVariable String name, @RequestParam LocalDateTime queryAt){
        return reactiveVentDb.getCollection(name).
            identifyAll(queryAt).
            map(id -> mapperFacade.map(id, IdView.class));
    }

    @GetMapping(OBJECT)
    public Mono<ObjectView> get(@PathVariable String name, @PathVariable String id, @RequestParam LocalDateTime queryAt){
        return reactiveVentDb.getCollection(name).
            get(new VentId(id), queryAt).
            map(snapshot -> mapperFacade.map(snapshot, ObjectView.class));
    }

    //todo execute query

    @GetMapping(OBJECTS)
    public Flux<ObjectView> getAll(@PathVariable String name, @RequestParam LocalDateTime queryAt){
        return reactiveVentDb.getCollection(name).
            getAll(queryAt).
            map(snapshot -> mapperFacade.map(snapshot, ObjectView.class));
    }
}
