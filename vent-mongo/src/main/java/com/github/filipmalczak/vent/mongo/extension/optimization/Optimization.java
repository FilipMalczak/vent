package com.github.filipmalczak.vent.mongo.extension.optimization;

import com.github.filipmalczak.vent.mongo.VentDb;
import com.github.filipmalczak.vent.mongo.extension.optimization.impl.OptimizerImpl;
import com.github.filipmalczak.vent.mongo.extension.optimization.plan.OptimizationPlan;
import com.github.filipmalczak.vent.mongo.extension.scan.PageStream;
import com.github.filipmalczak.vent.mongo.extension.scheduling.ScheduleScheme;
import com.github.filipmalczak.vent.mongo.extension.scheduling.Scheduler;
import com.github.filipmalczak.vent.mongo.factory.FactoryPlugin;
import com.github.filipmalczak.vent.mongo.factory.ResultWithAPI;
import com.github.filipmalczak.vent.mongo.service.VentServices;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

//todo: test this in any way at all!
@AllArgsConstructor
@Slf4j
public class Optimization implements FactoryPlugin<VentDb, VentServices> {

    private OptimizationPlan plan;

    @Override
    public ResultWithAPI<VentDb, VentServices> process(ResultWithAPI<VentDb, VentServices> exposedInstance) {
        Optimizer optimizer = new OptimizerImpl(exposedInstance.getExtensionAPI());
        PageStream pageStream = new PageStream(exposedInstance.getExtensionAPI());
        plan.getScheduleScheme().task(() -> {
            if (plan.getName() != null)
                log.info("Performing optimization according to plan '"+plan.getName()+"'");
            Flux.fromStream(plan.getPlanActions().stream())
                .flatMap(a -> a.act(pageStream, optimizer))
                .count()
                .subscribe(cnt ->
                    log.info(
                        ( cnt > 0 ? "Optimized "+cnt+" pages" : "No pages found for optimization" ) +
                            (plan.getName() != null ? " according to plan "+plan.getName() : "")
                    )
                );
        });
        return exposedInstance;
    }

    public static OptimizationPlan plan(@NonNull ScheduleScheme scheduleScheme){
        return new OptimizationPlan(scheduleScheme);
    }

    public static OptimizationPlan plan(@NonNull String name, @NonNull ScheduleScheme scheduleScheme){
        return new OptimizationPlan(name, scheduleScheme);
    }
}
