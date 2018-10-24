package com.github.filipmalczak.vent.mongo.extension.scheduling;

import lombok.AllArgsConstructor;

import java.time.Duration;

public interface FluentScheduler extends Scheduler<Duration>, WithEvery<ScheduleScheme> {
    @AllArgsConstructor
    class TaskClosure implements WithEvery<Schedule> {
        private FluentScheduler scheduler;
        private Runnable task;

        @Override
        public Schedule every(Duration duration){
            return scheduler.every(duration).task(task);
        }
    }

    default ScheduleScheme every(Duration duration){
        return schedule(duration);
    }

    default TaskClosure schedule(Runnable task){
        return new TaskClosure(this, task);
    }
}
