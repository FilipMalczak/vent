package com.github.filipmalczak.vent.mongo.extension.scheduling;

public interface Scheduler<ScheduleDefinition> {
    Schedule schedule(ScheduleDefinition scheduleDefinition, Runnable task);

    default ScheduleScheme schedule(ScheduleDefinition scheduleDefinition){
        return task -> schedule(scheduleDefinition, task);
    }
}
