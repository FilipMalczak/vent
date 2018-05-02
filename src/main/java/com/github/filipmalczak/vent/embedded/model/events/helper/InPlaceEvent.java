package com.github.filipmalczak.vent.embedded.model.events.helper;

import java.time.LocalDateTime;
import java.util.Map;

public abstract class InPlaceEvent extends TimestampedEvent {
    protected InPlaceEvent(LocalDateTime occuredOn) {
        super(occuredOn);
    }

    @Override
    public Map apply(Map map) {
        modify(map);
        return map;
    }

    protected abstract void modify(Map map);
}
