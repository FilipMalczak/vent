package com.github.filipmalczak.vent.mongo.extension.scheduling;

@FunctionalInterface
public interface ScheduleScheme {
    Schedule task(Runnable task);
}
