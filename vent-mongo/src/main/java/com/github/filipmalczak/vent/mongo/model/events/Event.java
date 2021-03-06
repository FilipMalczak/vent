package com.github.filipmalczak.vent.mongo.model.events;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

/**
 * This is a timestamped function that turns object from state before this event occured to state right after that
 * happened.
 *
 * The argument can be modified in place and returned - there is no need for deep copy, as it will exist only in
 * scope of applying chain of events by some SnapshotRenderer.
 */
public interface Event<E extends Event<E>> extends Function<Map, Map> {
    LocalDateTime getOccuredOn();
    E withOccuredOn(LocalDateTime occuredOn);
}
