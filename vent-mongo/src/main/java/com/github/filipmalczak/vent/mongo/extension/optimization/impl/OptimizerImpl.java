package com.github.filipmalczak.vent.mongo.extension.optimization.impl;

import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.mongo.extension.optimization.Optimizer;
import com.github.filipmalczak.vent.mongo.extension.scan.model.IdentifiablePage;
import com.github.filipmalczak.vent.mongo.model.Page;
import com.github.filipmalczak.vent.mongo.service.VentServices;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static reactor.core.publisher.Mono.just;


@AllArgsConstructor
@Slf4j
public class OptimizerImpl implements Optimizer {
    private final VentServices ventServices;

    @Override
    public Mono<Success> optimize(IdentifiablePage page) {
        LocalDateTime now = ventServices.getTemporalService().now();
        log.debug("Optimizing "+page+" at "+now);
        ObjectSnapshot snapshot = ventServices.getSnapshotService().render(page.getPage(), now);
        Mono<Page> next = ventServices.getPageService().createEmptyNextPage(page.getMongoCollectionName(), page.getPage(), now);
        return next
            .map(n -> {
                log.debug("Optimized page "+page+" to "+n);
                n.setInitialState(snapshot.getState());
                return n;
            })
            .flatMap(n -> ventServices.getMongoOperations().save(n, page.getMongoCollectionName()))
            .then(just(Success.SUCCESS));
    }
}
