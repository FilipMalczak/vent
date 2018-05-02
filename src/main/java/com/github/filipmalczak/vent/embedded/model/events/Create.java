package com.github.filipmalczak.vent.embedded.model.events;

import com.github.filipmalczak.vent.embedded.model.events.helper.TimestampedEvent;

import java.time.LocalDateTime;
import java.util.Map;

public class Create extends TimestampedEvent{
    private Map initialState;

    Create(Map initialState, LocalDateTime occuredOn) {
        super(occuredOn);
        this.initialState = initialState;
    }

    @Override
    public Map apply(Map map) {
        if (map != null)
            throw new RuntimeException("Create must happen first!"); //todo
        return initialState;
    }
}
