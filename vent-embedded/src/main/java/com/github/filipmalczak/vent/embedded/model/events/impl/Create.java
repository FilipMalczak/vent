package com.github.filipmalczak.vent.embedded.model.events.impl;

import com.github.filipmalczak.vent.embedded.model.events.helper.TimestampedEvent;

import java.time.LocalDateTime;
import java.util.Map;

public class Create extends TimestampedEvent<Create>{
    private Map initialState;

    Create(Map initialState, LocalDateTime occuredOn) {
        super(occuredOn);
        this.initialState = initialState;
    }

    @Override
    public Map apply(Map map) {
        if (map != null)
            throw new IllegalStateException("Create must happen first!");
        return initialState;
    }

    @Override
    public Create withOccuredOn(LocalDateTime occuredOn) {
        return new Create(initialState, occuredOn);
    }
}
