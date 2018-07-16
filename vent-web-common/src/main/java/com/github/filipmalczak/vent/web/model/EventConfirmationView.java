package com.github.filipmalczak.vent.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventConfirmationView {
    private IdView id;
    private LocalDateTime happenedOn;
}
