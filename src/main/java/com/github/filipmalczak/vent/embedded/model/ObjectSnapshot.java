package com.github.filipmalczak.vent.embedded.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Data
public class ObjectSnapshot {
    private Map state;
    private long version;
    private LocalDateTime queryTime;
    private LocalDateTime lastUpdate;
}
