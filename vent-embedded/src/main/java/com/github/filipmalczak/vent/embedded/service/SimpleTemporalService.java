package com.github.filipmalczak.vent.embedded.service;


import com.github.filipmalczak.vent.api.temporal.TemporalService;

import java.time.LocalDateTime;

public class SimpleTemporalService implements TemporalService {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
