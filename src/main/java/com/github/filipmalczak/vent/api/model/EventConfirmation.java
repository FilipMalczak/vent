package com.github.filipmalczak.vent.api.model;

import lombok.Value;

import java.time.LocalDateTime;


@Value
public class EventConfirmation {
    private VentId ventId;
    private LocalDateTime happenedOn;
}
