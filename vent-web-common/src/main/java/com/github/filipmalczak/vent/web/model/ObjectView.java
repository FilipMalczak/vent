package com.github.filipmalczak.vent.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectView {
    private IdView ventId;
    private Map view;
    private long version;
    private LocalDateTime queryTime;
    private LocalDateTime lastUpdate;
}
