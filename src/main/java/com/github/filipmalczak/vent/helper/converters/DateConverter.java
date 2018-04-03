package com.github.filipmalczak.vent.helper.converters;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateConverter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String convert(LocalDateTime timestamp){
        return timestamp.format(FORMATTER);
    }
}
