package com.github.filipmalczak.vent.mongo.extension.scan;

import com.github.filipmalczak.vent.helper.MemoizedSupplier;
import com.github.filipmalczak.vent.mongo.extension.scan.model.CollectionRepresentation;
import com.github.filipmalczak.vent.mongo.model.CollectionDescriptor;
import com.github.filipmalczak.vent.mongo.service.VentServices;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

@AllArgsConstructor
@Slf4j
public class CollectionStream {
    private VentServices ventServices;

    public Mono<CollectionDescriptor> ofCollection(String collectionName){
        return ventServices.getCollectionService().getDescriptor(collectionName);
    }

    public Mono<CollectionDescriptor> ofCollection(String collectionName, Predicate<CollectionRepresentation> predicate){
        return ofCollection(collectionName).filter(predicate(predicate));
    }

    public Flux<CollectionDescriptor> ofCollections(){
        return ventServices.getCollectionService().getAllCollections();
    }

    public Flux<CollectionDescriptor> ofCollections(Predicate<CollectionRepresentation> predicate){
        return ofCollections().filter(predicate(predicate));
    }

    private CollectionRepresentation representation(CollectionDescriptor descriptor){
        Supplier<LocalDateTime> now = MemoizedSupplier.over(ventServices.getTemporalService());
        return new CollectionRepresentation(
            descriptor.getVentCollectionName(),
            descriptor.getPreviousPeriods().size(),
            descriptor.getCurrentPeriod().duration(now),
            descriptor.duration(now)
        );
    }

    private Predicate<CollectionDescriptor> predicate(Predicate<CollectionRepresentation> representationPredicate){
        return desc -> representationPredicate.test(representation(desc));
    }
}
