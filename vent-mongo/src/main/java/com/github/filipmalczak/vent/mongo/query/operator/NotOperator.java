package com.github.filipmalczak.vent.mongo.query.operator;

import lombok.Value;

import java.util.Map;
import java.util.function.Predicate;

import static com.github.filipmalczak.vent.helper.Struct.pair;

@Value(staticConstructor = "of")
public class NotOperator implements Operator {
    private final Operator negated;

    @Override
    public Map<String, Object> toMongoInitialStateCriteria() {
        return pair("$not", negated.toMongoInitialStateCriteria());
    }

    @Override
    public Map<String, Object> toMongoEventCriteria() {
        return pair("$not", negated.toMongoEventCriteria());
    }

    @Override
    public Predicate<Map> toRuntimeCriteria() {
        return negated.toRuntimeCriteria().negate();
    }
}
