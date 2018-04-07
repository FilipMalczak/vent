package com.github.filipmalczak.vent.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Map;

@Value
@Builder
public class VentedObject {
    private Map object;
    private LocalDateTime timestamp;
}
