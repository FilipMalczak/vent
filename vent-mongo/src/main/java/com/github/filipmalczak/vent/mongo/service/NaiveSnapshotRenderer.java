package com.github.filipmalczak.vent.mongo.service;

import com.github.filipmalczak.vent.mongo.model.events.Event;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.function.Function.identity;

/**
 * This renderer is naive because it doesn't take into account facts like "only the last PutValue(path, val) impacts output
 * for given path". In other words, this renderer just applies all the events, without trying to use some
 * semantic knowledge about them.
 */
public class NaiveSnapshotRenderer implements SnapshotRenderer {

    @Override
    public Map render(Map initialSnapshot, List<Event> events) {
        return events.stream().
            map(e -> (Function<Map, Map>) e).
            reduce(Function::andThen).orElse(identity()).
            apply(initialSnapshot);
    }
}
