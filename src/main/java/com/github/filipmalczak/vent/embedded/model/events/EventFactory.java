package com.github.filipmalczak.vent.embedded.model.events;

import com.github.filipmalczak.vent.embedded.service.TemporalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EventFactory {
    @Autowired
    private TemporalService temporalService;

    public Create create(Map initialState){
        return new Create(initialState, temporalService.now());
    }

    public PutValue putValue(String path, Object value){
        return new PutValue(path, value, temporalService.now());
    }
}
