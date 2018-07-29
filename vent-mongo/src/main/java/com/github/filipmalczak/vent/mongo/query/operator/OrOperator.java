package com.github.filipmalczak.vent.mongo.query.operator;

import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.github.filipmalczak.vent.helper.Struct.pair;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrOperator implements Operator {
    @Singular //todo enforce non-empty
    private final List<Operator> operands;

    @Override
    public Map<String, Object> toMongoInitialStateCriteria() {
        return pair("$or", operands.stream().map(Operator::toMongoInitialStateCriteria));
    }

    @Override
    public Map<String, Object> toMongoEventCriteria() {
        return pair("$or", operands.stream().map(Operator::toMongoEventCriteria));
    }

    @Override
    public Predicate<Map> toRuntimeCriteria() {
        return operands.stream().map(Operator::toRuntimeCriteria).reduce(Predicate::or).orElse(o -> true);
    }
}
