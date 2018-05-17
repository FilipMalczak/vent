package com.github.filipmalczak.vent.embedded;

import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.embedded.model.VentDbDescriptor;
import com.github.filipmalczak.vent.embedded.model.events.impl.EventFactory;
import com.github.filipmalczak.vent.embedded.service.MongoQueryPreparator;
import com.github.filipmalczak.vent.embedded.service.PageService;
import com.github.filipmalczak.vent.embedded.service.SnapshotService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

import static com.github.filipmalczak.vent.api.model.Success.NO_OP_SUCCESS;
import static com.github.filipmalczak.vent.api.model.Success.SUCCESS;
import static reactor.core.publisher.Mono.just;

//todo define API status for embedded stuff; probably provide single factory-like entry point
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class EmbeddedReactiveVentDb implements ReactiveVentDb {
    private @NonNull PageService pageService;

    private @NonNull EventFactory eventFactory;

    private @NonNull SnapshotService snapshotService;

    private @NonNull MongoQueryPreparator mongoQueryPreparator;

    private @NonNull ReactiveMongoTemplate mongoTemplate;

    private static final String VENT_DESCRIPTOR_COLLECTION = "vent.descriptor";

    @PostConstruct
    @Override
    public void initialize() {
        mongoTemplate.
            collectionExists(VENT_DESCRIPTOR_COLLECTION).
            filter(x -> !x).
            flatMap(b -> initializeDescriptor()).
            subscribe();
    }

    private Mono<VentDbDescriptor> initializeDescriptor(){
        return mongoTemplate.insert(new VentDbDescriptor(), VENT_DESCRIPTOR_COLLECTION);
    }

    @Override
    public ReactiveVentCollection getCollection(String collectionName) {
        manage(collectionName);
        return new EmbeddedReactiveVentCollection(collectionName, pageService, eventFactory, snapshotService, mongoQueryPreparator, mongoTemplate);
    }

    @Override
    public Mono<Success> optimizePages(SuggestionStrength strength, OptimizationType type) {
        return null;
    }

    @Override
    public Flux<String> getManagedCollections() {
        return getDescriptor().flatMapIterable(VentDbDescriptor::getManagedCollections);
    }

    @Override
    public Mono<Success> manage(String collectionName) {
        return getDescriptor().
            filter(d -> d.manage(collectionName)).
            map(this::saveDescriptor).
            map(x -> SUCCESS).
            switchIfEmpty(just(NO_OP_SUCCESS));
    }

    private Mono<VentDbDescriptor> getDescriptor(){
        return mongoTemplate.
            findAll(VentDbDescriptor.class, VENT_DESCRIPTOR_COLLECTION).
            singleOrEmpty().
//            onErrorMap((IndexOutOfBoundsException e) -> new RuntimeException(e)). //todo more than one descriptor should be treated as error
            switchIfEmpty(initializeDescriptor());
    }

    private Mono<VentDbDescriptor> saveDescriptor(VentDbDescriptor descriptor){
        return mongoTemplate.save(descriptor, VENT_DESCRIPTOR_COLLECTION);
    }
}
