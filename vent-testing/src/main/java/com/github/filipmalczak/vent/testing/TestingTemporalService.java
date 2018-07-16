package com.github.filipmalczak.vent.testing;

import com.github.filipmalczak.vent.api.temporal.TemporalService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class TestingTemporalService implements TemporalService {
    private List<LocalDateTime> queueToReturn = new ArrayList<>();
    @Autowired(required = false)
    private StackTracer stackTracer;

    @SneakyThrows
    public <C extends Collection<LocalDateTime>> void withResults(C times, Runnable runnable){
        int initialQueueSize = queueToReturn.size();
        addResults(times);
        Throwable originalThrowable = null;
        try {
            runnable.run();
        } catch (Throwable t) {
            originalThrowable = t;
        } finally {
            try {
                assertEquals(initialQueueSize, queueToReturn.size(), "All times have to be used! (left: "+queueToReturn+")");
            } catch (Throwable t) {
                if (originalThrowable != null)
                    t.addSuppressed(originalThrowable);
                throw t;
            } finally {
                if (initialQueueSize > 0) {
                    if (initialQueueSize < queueToReturn.size())
                        queueToReturn = queueToReturn.subList(0, initialQueueSize);
                } else
                    queueToReturn.clear();
            }
            //"assert empty queue" passed, lets check whether anything was thrown from runnable
            if (originalThrowable != null)
                throw originalThrowable;
        }
    }

    public void addResults(Collection<LocalDateTime> times){
        if (stackTracer != null) {
            log.info("addResults("+times+"): Call hierarchy: " + stackTracer.getCurrentHierarchy());
        }
        queueToReturn.addAll(times);
        if (stackTracer != null) {
            log.info("current queue: "+queueToReturn);
        }

    }

    public void addResults(LocalDateTime... times){
        addResults(asList(times));
    }

    public void clear(){
        queueToReturn.clear();
    }

    public LocalDateTime peekNow(){
        if (stackTracer != null){
            log.info("peekNow(): Call hierarchy: "+stackTracer.getCurrentHierarchy());
        }
        return queueToReturn.get(0);
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