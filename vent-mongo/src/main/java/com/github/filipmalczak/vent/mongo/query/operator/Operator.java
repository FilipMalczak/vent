package com.github.filipmalczak.vent.mongo.query.operator;

import java.util.Map;
import java.util.function.Predicate;

public interface Operator {
    /**
     * This is gonna be mongo in Page query, applying to initialState like { initialState: [result] }
     * Should not check timestamps (orchestration of operators will take care of that).
     */
    Map<String, Object> toMongoInitialStateCriteria();

    /**
     * This is gonna be mongo in Page query, applying to events like { events: { $elemMatch: [result] } }
     * Should not check timestamps (orchestration of operators will take care of that).
     */
    Map<String, Object> toMongoEventCriteria();

    /**
     * Once we filter out pages on Mongo level with queries above, we choose those that really match the query
     * with this predicate (applied to rendered snapshot state).
     */
    Predicate<Map> toRuntimeCriteria();
}
