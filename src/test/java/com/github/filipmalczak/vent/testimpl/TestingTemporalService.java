package com.github.filipmalczak.vent.testimpl;

import com.github.filipmalczak.vent.embedded.service.TemporalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@Slf4j
public class TestingTemporalService implements TemporalService {
    private List<LocalDateTime> queueToReturn = new ArrayList<>();
    @Autowired(required = false)
    private StackTracer stackTracer;

    public void addResults(List<LocalDateTime> times){
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