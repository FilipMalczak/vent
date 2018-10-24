package com.github.filipmalczak.vent.mongo.extension.optimization.plan;

import com.github.filipmalczak.vent.mongo.extension.optimization.Optimization;
import com.github.filipmalczak.vent.mongo.extension.scheduling.ScheduleScheme;
import lombok.*;

import java.util.LinkedList;
import java.util.List;

public class OptimizationPlan {
    @Getter private String name;
    @Getter @NonNull
    private ScheduleScheme scheduleScheme;
    @Getter
    private List<PlanAction> planActions = new LinkedList<>();

    public OptimizationPlan(ScheduleScheme scheduleScheme) {
        this.scheduleScheme = scheduleScheme;
    }

    public OptimizationPlan(String name, ScheduleScheme scheduleScheme) {
        this.name = name;
        this.scheduleScheme = scheduleScheme;
    }

    public OptimizationPlan withAction(PlanAction action){
        planActions.add(action);
        return this;
    }

    public Optimization endPlan(){
        return new Optimization(this);
    }


}
