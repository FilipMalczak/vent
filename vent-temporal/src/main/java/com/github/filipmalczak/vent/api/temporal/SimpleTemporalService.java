package com.github.filipmalczak.vent.api.temporal;


import java.time.LocalDateTime;

public class SimpleTemporalService implements TemporalService {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
