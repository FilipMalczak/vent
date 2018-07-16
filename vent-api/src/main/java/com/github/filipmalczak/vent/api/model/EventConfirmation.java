package com.github.filipmalczak.vent.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;


//@Value
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventConfirmation {
    private VentId ventId;
    private LocalDateTime happenedOn;
    //todo: add event type; optional candidates: event id, object version, page id/hash
}
