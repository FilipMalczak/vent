package com.github.filipmalczak.vent.mongo.extension.scan;

import com.github.filipmalczak.vent.mongo.extension.scan.model.IdentifiablePage;
import com.github.filipmalczak.vent.mongo.extension.scan.model.PageRepresentation;
import com.github.filipmalczak.vent.mongo.model.Page;
import com.github.filipmalczak.vent.mongo.service.VentServices;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Predicate;

import static java.util.Optional.ofNullable;

@AllArgsConstructor
@Slf4j
public class PageStream {
    private VentServices ventServices;

    public Flux<IdentifiablePage> ofCurrentPages(String collectionName){
        return ventServices.getCollectionService().
            getMongoCollectionNameForCurrentPeriod(collectionName).
            flatMapMany(mongoName ->
                ventServices.getPageService().
                    allPages(mongoName).
                    filter(Page::currentPage).
                    map(p -> new IdentifiablePage(collectionName, mongoName, p))
            );
    }

    public Flux<IdentifiablePage> ofCurrentPages(){
        return ventServices.getCollectionService().
            getAllCollectionNames().
            flatMap(this::ofCurrentPages);
    }

    public Flux<IdentifiablePage> ofCurrentPages(Predicate<PageRepresentation> predicate){
        return filterOut(ofCurrentPages(), predicate);
    }

    public Flux<IdentifiablePage> ofCurrentPages(String collectionName, Predicate<PageRepresentation> predicate){
        return filterOut(ofCurrentPages(collectionName), predicate);
    }

    private Flux<IdentifiablePage> filterOut(Flux<IdentifiablePage> pages, Predicate<PageRepresentation> predicate){
        return pages.filter(p ->
            predicate.test(representation(p.getPage()))
        );
    }

    private PageRepresentation representation(Page page){
        return new PageRepresentation(
            page.getObjectId(),
            page.getPageId(),
            page.firstPageOfHistory(),
            page.getStartingFrom(),
            Duration.between(
                page.getStartingFrom(),
                ofNullable(page.getNextPageFrom()).orElse(ventServices.getTemporalService().now())
            ),
            page.getInitialState() != null,
            page.getEvents().size()
        );
    }
}
