package com.github.filipmalczak.vent.api.general.object;

import com.github.filipmalczak.vent.api.model.VentId;

import java.util.Map;

public interface VentObjectWriteFacade<Confirmation> {
    VentId getId();

    Confirmation update(Map newState);
    Confirmation delete();

    Confirmation putValue(String path, Object value);
    Confirmation deleteValue(String path);
}
