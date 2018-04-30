package com.github.filipmalczak.vent.embedded.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@ConditionalOnMissingBean(TemporalService.class)
public class SimpleTemporalService implements TemporalService {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
