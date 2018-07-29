package com.github.filipmalczak.vent.mongo.model.events.impl;

import com.github.filipmalczak.vent.mongo.model.events.helper.TimestampedEvent;

import java.time.LocalDateTime;
import java.util.Map;

public class Update extends TimestampedEvent<Update> {
    private Map newValue;

    Update(Map newValue, LocalDateTime occuredOn) {
        super(occuredOn);
        this.newValue = newValue;
    }

    @Override
    public Map apply(Map map) {
        return newValue;
    }

    @Override
    public Update withOccuredOn(LocalDateTime occuredOn) {
        return new Update(newValue, occuredOn);
    }
}
