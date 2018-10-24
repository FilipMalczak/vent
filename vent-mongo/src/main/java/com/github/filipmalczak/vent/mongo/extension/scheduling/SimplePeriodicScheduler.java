package com.github.filipmalczak.vent.mongo.extension.scheduling;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.github.filipmalczak.vent.mongo.utils.DefaultsUitls.PROPERTY_PREFIX;

@AllArgsConstructor
@Slf4j
public class SimplePeriodicScheduler implements FluentScheduler {
    private static final String POOL_SIZE_PROPERTY = PROPERTY_PREFIX+"extension.schedule.pool.size";
    private static final int DEFAULT_POOL_SIZE;
    private static final ScheduledExecutorService SHARED_EXECUTOR;

    static {
        //todo fallback for non-int values
        DEFAULT_POOL_SIZE = Integer.parseInt(System.getProperty(POOL_SIZE_PROPERTY, "1"));
        //todo assert pool size > 0
        SHARED_EXECUTOR = newDefaultExecutor();
    }

    public static ScheduledExecutorService newDefaultExecutor(){
        return DEFAULT_POOL_SIZE == 1 ?
            Executors.newSingleThreadScheduledExecutor() :
            Executors.newScheduledThreadPool(DEFAULT_POOL_SIZE);
    }

    @NonNull
    private final ScheduledExecutorService executor;

    public SimplePeriodicScheduler() {
        this(newDefaultExecutor());
    }

    public static SimplePeriodicScheduler simple(){
        return new SimplePeriodicScheduler();
    }

    public static SimplePeriodicScheduler shared(){
        return new SimplePeriodicScheduler(SHARED_EXECUTOR);
    }

    @Override
    public Schedule schedule(Duration duration, Runnable task) {
        return new ScheduleImpl(task, duration.toNanos());
    }

    private class ScheduleImpl implements Schedule {
        private final Runnable task;
        private final long durationInNano;
        private ScheduledFuture nextRun;

        public ScheduleImpl(Runnable task, long durationInNano) {
            this.task = task;
            this.durationInNano = durationInNano;
            nextRun = scheduleRun();
        }

        private ScheduledFuture scheduleRun(){
            log.debug("Scheduling to run "+durationInNano+"us from now");
            return executor.schedule(this::executeRun, durationInNano, TimeUnit.NANOSECONDS);
        }

        @Synchronized
        private void executeRun(){
            log.debug("Executing run");
            task.run();
            log.debug("Run executed, scheduling another one");
            nextRun = scheduleRun();
        }

        @Override
        @Synchronized
        public void cancel() {
            if (!(nextRun.isCancelled() || nextRun.isDone()))
                nextRun.cancel(false);
        }
    }
}
