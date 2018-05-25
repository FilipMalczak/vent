package com.github.filipmalczak.vent.web.controller;

import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.embedded.service.TemporalService;
import com.github.filipmalczak.vent.web.model.CreateRequest;
import com.github.filipmalczak.vent.web.model.IdView;
import com.github.filipmalczak.vent.web.model.NewEventRequest;
import com.github.filipmalczak.vent.web.model.ObjectView;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static com.github.filipmalczak.vent.web.controller.CrudPaths.COLLECTION_PATH;
import static com.github.filipmalczak.vent.web.controller.CrudPaths.OBJECT_PATH;

@Controller
public class CrudController {
    @Autowired
    private ReactiveVentDb reactiveVentDb;

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private TemporalService temporalService;

    @PostMapping(COLLECTION_PATH)
    public Mono<IdView> create(@PathVariable String collection, @RequestBody CreateRequest request){
        return reactiveVentDb.getCollection(collection).
            create(Optional.
                ofNullable(request.getInitialState()).
                orElse(Collections.emptyMap())
            ).
            map(id -> mapperFacade.map(id, IdView.class));
    }

    @PutMapping(OBJECT_PATH)
    public Mono<Void> update(@PathVariable String collection, @PathVariable VentId id, @RequestBody NewEventRequest request){
        return Mono.error(new RuntimeException("")); //todo
    }

    @DeleteMapping(OBJECT_PATH)
    public Mono<Void> delete(@PathVariable String collection, @PathVariable VentId id){
        return Mono.error(new RuntimeException("")); //todo
    }

    @GetMapping(OBJECT_PATH)
    public Mono<ObjectView> get(@PathVariable String collection, @PathVariable VentId id, @RequestParam(value = "at", required = false) Optional<LocalDateTime> at){
        LocalDateTime queryAt = at.orElse(temporalService.now());
        return reactiveVentDb.getCollection(collection).
            get(id, queryAt).
            map(snapshot -> mapperFacade.map(snapshot, ObjectView.class));
    }
}
