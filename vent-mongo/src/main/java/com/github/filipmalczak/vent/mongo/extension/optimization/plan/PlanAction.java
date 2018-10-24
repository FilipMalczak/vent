package com.github.filipmalczak.vent.mongo.extension.optimization.plan;

import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.mongo.extension.optimization.Optimizer;
import com.github.filipmalczak.vent.mongo.extension.scan.PageStream;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface PlanAction {
    Flux<Success> act(PageStream pageStream, Optimizer optimizer);
}
