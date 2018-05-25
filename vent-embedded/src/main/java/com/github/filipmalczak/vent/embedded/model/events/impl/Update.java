package com.github.filipmalczak.vent.embedded.model.events.impl;

import com.github.filipmalczak.vent.embedded.model.events.helper.TimestampedEvent;

import java.time.LocalDateTime;
import java.util.Map;

public class Update extends TimestampedEvent {
    private Map newValue;

    Update(Map newValue, LocalDateTime occuredOn) {
        super(occuredOn);
        this.newValue = newValue;
    }

    @Override
    public Map apply(Map map) {
        return newValue;
    }
}
