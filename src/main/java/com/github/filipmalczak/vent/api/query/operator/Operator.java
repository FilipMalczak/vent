package com.github.filipmalczak.vent.api.query.operator;

import com.github.filipmalczak.vent.api.ObjectSnapshot;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Predicate;

public interface Operator {
    /**
     * This is gonna be embedded in Page query, applying to initialState like { initialState: [result] }
     * Should not check timestamps (orchestration of operators will take care of that).
     */
    Map<String, Object> toMongoInitialStateCriteria();
    /**
     * This is gonna be embedded in Page query, applying to events like { events: { $elemMatch: [result] } }
     * Should not check timestamps (orchestration of operators will take care of that).
     */
    Map<String, Object> toMongoEventCriteria();
    Predicate<Map> toRuntimeCriteria();
}
