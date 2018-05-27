package com.github.filipmalczak.vent.embedded.model.events.impl;

import com.github.filipmalczak.vent.embedded.model.events.helper.TimestampedEvent;

import java.time.LocalDateTime;
import java.util.Map;

public class Delete extends TimestampedEvent {
    protected Delete(LocalDateTime occuredOn) {
        super(occuredOn);
    }

    @Override
    public Map apply(Map map) {
        return null;
    }
}
