package com.github.filipmalczak.vent.embedded.model.events.helper;

import com.github.filipmalczak.vent.embedded.model.events.Event;
import lombok.Getter;

import java.time.LocalDateTime;

public abstract class TimestampedEvent implements Event{
    @Getter private final LocalDateTime occuredOn;

    protected TimestampedEvent(LocalDateTime occuredOn) {
        this.occuredOn = occuredOn;
    }
}
