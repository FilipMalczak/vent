package com.github.filipmalczak.vent.embedded.model.events.helper;

import com.github.filipmalczak.vent.embedded.model.events.Event;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@EqualsAndHashCode
public abstract class TimestampedEvent<E extends TimestampedEvent<E>> implements Event<E>{
    @Getter private final LocalDateTime occuredOn;

    protected TimestampedEvent(LocalDateTime occuredOn) {
        this.occuredOn = occuredOn;
    }
}
