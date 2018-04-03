package com.github.filipmalczak.vent.service.impl;

import com.github.filipmalczak.vent.service.TimestampService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BasicTimestampService implements TimestampService {
    @Override
    public LocalDateTime now() {
        //todo add timezone from application.yml
        return LocalDateTime.now();
    }
}
