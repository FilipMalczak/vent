package com.github.filipmalczak.vent.embedded.service;

import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.embedded.exception.IllegalVentStateException;
import com.github.filipmalczak.vent.embedded.model.Page;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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
@Component
public class PageOptimizer {
    @Autowired
    private ReactiveVentDb ventDb;

    @Autowired
    private TemporalService temporalService;

    @Autowired
    private PageService pageService;

    @Autowired
    private SnapshotService snapshotService;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Value("${vent.pageOptimization.partial.olderThan.value}")
    private int olderThanValue;

    @Value("${vent.pageOptimization.partial.olderThan.unit}")
    private ChronoUnit olderThanUnit;

    @Value("${vent.pageOptimization.partial.crowding}")
    private int partialCrowdingThreshold;

    @Value("${vent.pageOptimization.full.crowding}")
    private int fullCrowdingThreshold;

    private ExecutorService executorService = Executors.newFixedThreadPool(4); //todo take the value from config
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
    @Scheduled(cron = "${vent.pageOptimization.full.schedule}")
    @Synchronized public void performFullOptimization(){
        LocalDateTime now = temporalService.now();
        performOptimization(fullOptimizationPredicateFactory, now);
    }

    /**
     * Partial optimization is performed for all objects with more events that crowding threshold or older than some
     * configured age. This is supposed to boost performance when querying and getting objects, by keeping event lists
     * short enough and almost empty for stale objects.
     */
    @Scheduled(cron = "${vent.pageOptimization.partial.schedule}")
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
            createEmptyNextPage(collectionName, page).
            map(p -> {
                p.setInitialState(snapshotService.render(page, at).getState());
                return p;
            }).
            flatMap(p -> mongoTemplate.save(p, collectionName)).
            block();
    }
}
