package com.github.filipmalczak.vent.embedded.model.events.impl;

import com.github.filipmalczak.vent.embedded.model.events.Event;
import com.github.filipmalczak.vent.embedded.model.events.helper.TimestampedEvent;

import java.time.LocalDateTime;
import java.util.Map;

public class Delete extends TimestampedEvent<Delete> {
    protected Delete(LocalDateTime occuredOn) {
        super(occuredOn);
    }

    @Override
    public Map apply(Map map) {
        return null;
    }

    @Override
    public Delete withOccuredOn(LocalDateTime occuredOn) {
        return new Delete(occuredOn);
    }
}
