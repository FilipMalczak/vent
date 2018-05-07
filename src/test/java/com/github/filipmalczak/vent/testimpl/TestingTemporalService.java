package com.github.filipmalczak.vent.testimpl;

import com.github.filipmalczak.vent.embedded.service.TemporalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class TestingTemporalService implements TemporalService {
    private List<LocalDateTime> queueToReturn = new ArrayList<>();
    @Autowired(required = false)
    private StackTracer stackTracer;

    public <C extends Collection<LocalDateTime>> void withResults(C times, Runnable onCurrentNow){
        boolean queueWasInitiallyEmpty = queueToReturn.isEmpty();
        addResults(times);
        try {
            onCurrentNow.run();
        } finally {
            if (queueWasInitiallyEmpty) {
                try {
                    assertTrue(queueToReturn.isEmpty());
                } catch (Throwable t) {
                    throw t;
                } finally {
                    queueToReturn.clear();
                }
            }
        }
    }

    public void addResults(Collection<LocalDateTime> times){
        queueToReturn.addAll(times);
    }
    public void addResults(LocalDateTime... times){
        addResults(asList(times));
    }

    @Override
    public LocalDateTime now() {
        if (stackTracer != null){
            log.info("now(): Call hierarchy: "+stackTracer.getCurrentHierarchy());
        }
        try {
            return queueToReturn.get(0);
        } finally {
            queueToReturn.remove(0);
        }
    }
}