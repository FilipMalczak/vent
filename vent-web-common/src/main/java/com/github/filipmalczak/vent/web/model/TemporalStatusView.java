package com.github.filipmalczak.vent.web.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Builder
public class TemporalStatusView {
    private LocalDateTime now;
    private ZoneId timezone;
}
