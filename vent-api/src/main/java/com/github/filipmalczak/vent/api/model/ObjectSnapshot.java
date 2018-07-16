package com.github.filipmalczak.vent.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectSnapshot {
    private VentId ventId;
    private Map state;
    private long version;
    private LocalDateTime queryTime;
    private LocalDateTime lastUpdate;
}
