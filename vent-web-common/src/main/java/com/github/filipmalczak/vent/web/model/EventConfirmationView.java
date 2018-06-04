package com.github.filipmalczak.vent.web.model;

import com.github.filipmalczak.vent.api.model.VentId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventConfirmationView {
    private IdView id;
    private LocalDateTime happenedOn;
}
