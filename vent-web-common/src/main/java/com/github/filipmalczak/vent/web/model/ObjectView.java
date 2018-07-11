package com.github.filipmalczak.vent.web.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ObjectView {
    private IdView ventId;
    private Map view;
    private long version;
    private LocalDateTime queryTime;
    private LocalDateTime lastUpdate;
}
