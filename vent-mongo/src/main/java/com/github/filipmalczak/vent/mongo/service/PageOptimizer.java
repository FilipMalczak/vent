package com.github.filipmalczak.vent.mongo.service;

import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.mongo.exception.IllegalVentStateException;
import com.github.filipmalczak.vent.mongo.model.Page;
import lombok.Synchronized;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Page optimization is taking current page for an object and if there are any events on that page, calculating current
 * state of the object and creating new page with that initial state.
 */
//todo consider crowding optimization on "put value"
//todo move scheduling out of here
//todo figure out some more flexible criteria for partial optimization; it would be nice to provide extension point for that
public class PageOptimizer {
    private ReactiveVentDb<?, ?, ?> ventDb;
    private TemporalService temporalService;
    private PageService pageService;
    private SnapshotService snapshotService;
    private ReactiveMongoOperations mongoOperations;
    private int olderThanValue;
    private ChronoUnit olderThanUnit;
    private int partialCrowdingThreshold;
    private int fullCrowdingThreshold;
    //todo take the value from config or system props
    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private Function<LocalDateTime, Predicate<Page>> fullOptimizationPredicateFactory;
    private Function<LocalDateTime, Predicate<Page>> partialOptimizationPredicateFactory;

    //todo ugly code - rewrite or at least document
    @PostConstruct
    public void setUp(){
        //todo possibility to turn off partial optimization and maybe full too
        if (olderThanValue <= 0)
            throw new IllegalVentStateException(); //todo
        fullOptimizationPredicateFactory = fullCrowdingThreshold > 0 ?
            l -> (p -> p.getEvents().size() >= fullCrowdingThreshold):
            l -> (p -> true);
        Duration olderThan = Duration.of(olderThanValue, olderThanUnit);
        partialOptimizationPredicateFactory = partialCrowdingThreshold > 0 ?
            l -> (p -> p.getEvents().size() >= partialCrowdingThreshold ||
                p.getEvents().get(p.getEvents().size()-1).getOccuredOn().plus(olderThan).isBefore(l)) :
            l -> (p -> p.getEvents().get(p.getEvents().size()-1).getOccuredOn().plus(olderThan).isBefore(l));
    }

    /**
     * Full optimization is performed for all non-deleted objects. This shouldn't run too often, but should not be
     * skipped neither, to keep sane query and get times.
     */
//    @Scheduled(cron = "${vent.pageOptimization.full.schedule}")
    @Synchronized public void performFullOptimization(){
        LocalDateTime now = temporalService.now();
        performOptimization(fullOptimizationPredicateFactory, now);
    }

    /**
     * Partial optimization is performed for all objects with more events that crowding threshold or older than some
     * configured age. This is supposed to boost performance when querying and getting objects, by keeping event lists
     * short enough and almost empty for stale objects.
     */
//    @Scheduled(cron = "${vent.pageOptimization.partial.schedule}")
    @Synchronized public void performPartialOptimization(){
        LocalDateTime now = temporalService.now();
        performOptimization(partialOptimizationPredicateFactory, now);
    }

    private void performOptimization(Function<LocalDateTime, Predicate<Page>> filteringFactory, LocalDateTime at){
        Predicate<Page> filtering = filteringFactory.apply(at);
        wholeDbContent(at).filter(oi -> filtering.test(oi.getPage())).doOnNext(this::optimize).blockLast();
    }

    @lombok.Value(staticConstructor = "of")
    private static class OptimizationItem {
        private String collectionName;
        private Page page;
        private LocalDateTime at;
    }

    private Flux<OptimizationItem> wholeDbContent(LocalDateTime at){
        return ventDb.
            getManagedCollections().
            flatMap( c ->
                ventDb.
                    getCollection(c).
                    identifyAll(at).
                    flatMap(id -> pageService.currentPage(c, id)).
                    map(p -> OptimizationItem.of(c, p, at))
            );
    }

    private void optimize(OptimizationItem optimizationItem){
        optimize(optimizationItem.getCollectionName(), optimizationItem.getPage(), optimizationItem.getAt());
    }

    private void optimize(String collectionName, Page page, LocalDateTime at){
        pageService.
            createEmptyNextPage(collectionName, page, at).
            map(p -> {
                p.setInitialState(snapshotService.render(page, at).getState());
                return p;
            }).
            flatMap(p -> mongoOperations.save(p, collectionName)).
            block();
    }
}
