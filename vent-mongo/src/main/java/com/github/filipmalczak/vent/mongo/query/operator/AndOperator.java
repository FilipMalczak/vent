package com.github.filipmalczak.vent.mongo.query.operator;

import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.github.filipmalczak.vent.helper.Struct.map;
import static java.util.stream.Collectors.toList;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AndOperator implements Operator {
    //todo: enforce non-empty
    @Singular
    private final List<Operator> operands;

    @Override
    public Map<String, Object> toMongoInitialStateCriteria() {
        return map(operands.stream().map(Operator::toMongoInitialStateCriteria).collect(toList()));
    }

    @Override
    public Map<String, Object> toMongoEventCriteria() {
        return map(operands.stream().map(Operator::toMongoEventCriteria).collect(toList()));
    }

    @Override
    public Predicate<Map> toRuntimeCriteria() {
        return operands.stream().map(Operator::toRuntimeCriteria).reduce(Predicate::and).orElse(o -> true);
    }
}
