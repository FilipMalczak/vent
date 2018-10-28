package com.github.filipmalczak.vent.mongo.extension.optimization;

import com.github.filipmalczak.vent.mongo.extension.scheduling.SimplePeriodicScheduler;
import com.github.filipmalczak.vent.mongo.utils.DefaultsUitls;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;

@Slf4j
public class Defaults {
    public static final String PROPERTY_PREFIX = DefaultsUitls.PROPERTY_PREFIX+"extension.optimization.";

    public static final String PARTIAL_INTERVAL_PROPERTY = PROPERTY_PREFIX+"partial.value";
    public static final String PARTIAL_UNIT_PROPERTY = PROPERTY_PREFIX+"partial.unit";
    public static final long DEFAULT_PARTIAL_INTERVAL = 15;
    public static final ChronoUnit DEFAULT_PARTIAL_UNIT = MINUTES;
    public static final String FULL_INTERVAL_PROPERTY = PROPERTY_PREFIX+"full.value";
    public static final String FULL_UNIT_PROPERTY = PROPERTY_PREFIX+"full.unit";
    public static final long DEFAULT_FULL_INTERVAL = 1;
    public static final ChronoUnit DEFAULT_FULL_UNIT = HOURS;

    public static final String MIN_EVENT_COUNT_PROPERTY = PROPERTY_PREFIX+"partial.eventCount.min";
    public static final long DEFAULT_MIN_EVENT_COUNT = 100;

    public static final Duration partialOptimizationInterval;
    public static final Duration fullOptimizationInterval;

    public static final long minEventsNumber;

    static {
        partialOptimizationInterval = Duration.of(
            extract(PARTIAL_INTERVAL_PROPERTY, DEFAULT_PARTIAL_INTERVAL, Long::valueOf, ""+DEFAULT_PARTIAL_INTERVAL, "long"),
            extract(PARTIAL_UNIT_PROPERTY, DEFAULT_PARTIAL_UNIT, ChronoUnit::valueOf, ""+DEFAULT_PARTIAL_UNIT, "ChronoUnit")
        );

        fullOptimizationInterval = Duration.of(
            extract(FULL_INTERVAL_PROPERTY, DEFAULT_FULL_INTERVAL, Long::valueOf, ""+DEFAULT_FULL_INTERVAL, "long"),
            extract(FULL_UNIT_PROPERTY, DEFAULT_FULL_UNIT, ChronoUnit::valueOf, ""+DEFAULT_FULL_UNIT, "ChronoUnit")
        );

        minEventsNumber = extract(
            MIN_EVENT_COUNT_PROPERTY, DEFAULT_MIN_EVENT_COUNT, Long::valueOf, ""+DEFAULT_MIN_EVENT_COUNT, "long"
        );
    }

    private static <T> T extract(String property, T defaultValue, Function<String, T> converter, String defaultAsString, String typeName){
        String valueString = System.getProperty(property, defaultAsString);;
        try {
            return converter.apply(valueString);
        } catch (Throwable t){
            log.warn("Cannot parse "+valueString+" ("+property+") as "+typeName+", falling back to "+defaultAsString);
            return defaultValue;
        }
    }

    /**
     * Partial optimization is happening for every page that accumulated enough events.
     * Full optimization is happening for every page with non-empty event list that wasn't optimized during last partial
     * optimization.
     */
    //todo customizable minEventCount for full
    static {
        log.info("Default optimizations:");
        log.info("Partial: every "+partialOptimizationInterval+" ; min event count: "+minEventsNumber);
        log.info("Full:    every "+fullOptimizationInterval);
        defaultOptimizations = new Optimization[] {
            Optimization
                .plan(
                    "Partial",
                    SimplePeriodicScheduler.shared().every(partialOptimizationInterval)
                )
                .withAction((stream, opt) ->
                    stream.ofCurrentPages(p -> p.getEventCount() > minEventsNumber).flatMap(opt::optimize)
                )
                .endPlan(),
            Optimization
                .plan(
                    "Full",
                    SimplePeriodicScheduler.shared().every(fullOptimizationInterval)
                )
                .withAction((stream, opt) -> stream.ofCurrentPages(p ->
                        p.getEventCount() > 0 && p.getTimeSpan().compareTo(partialOptimizationInterval) > 0
                    ).flatMap(opt::optimize)
                )
                .endPlan()
        };
    }


    public static Optimization[] defaultOptimizations;
}
