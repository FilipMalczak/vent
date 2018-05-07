package com.github.filipmalczak.vent.embedded.model.events.impl;

import com.github.filipmalczak.vent.embedded.model.events.helper.InPlaceEvent;
import com.github.filipmalczak.vent.velvet.Velvet;

import java.time.LocalDateTime;
import java.util.Map;

public class DeleteValue extends InPlaceEvent {
    private final String path;

    DeleteValue(String path, LocalDateTime occuredOn) {
        super(occuredOn);
        this.path = path;
    }

    @Override
    protected void modify(Map map) {
        Velvet.parse(path).bind(map).delete();
    }
}
