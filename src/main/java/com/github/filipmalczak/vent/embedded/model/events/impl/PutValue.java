package com.github.filipmalczak.vent.embedded.model.events.impl;

import com.github.filipmalczak.vent.embedded.model.events.helper.InPlaceEvent;
import com.github.filipmalczak.vent.velvet.Velvet;

import java.time.LocalDateTime;
import java.util.Map;

public class PutValue extends InPlaceEvent {
    private final String path;
    private final Object value;

    PutValue(String path, Object value, LocalDateTime occuredOn) {
        super(occuredOn);
        this.path = path;
        this.value = value;
    }

    @Override
    protected void modify(Map map) {
        Velvet.parse(path).bind(map).set(value);
    }
}
