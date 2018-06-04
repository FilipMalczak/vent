package com.github.filipmalczak.vent.web.model;

import java.time.LocalDateTime;
import java.util.Map;

public class ObjectView {
    private IdView ventId;
    private Map view;
    private long version;
    private LocalDateTime queryTime;
    private LocalDateTime lastUpdate;
}
