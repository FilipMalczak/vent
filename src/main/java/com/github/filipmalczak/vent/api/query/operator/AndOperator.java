package com.github.filipmalczak.vent.api.query.operator;

import com.github.filipmalczak.vent.api.ObjectSnapshot;
import lombok.*;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.github.filipmalczak.vent.helper.Struct.pair;
import static java.util.stream.Collectors.toList;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AndOperator implements Operator {
    @Singular
    private final List<Operator> operands;

    @Override
    public Map<String, Object> toMongoInitialStateCriteria() {
        return pair("$and", operands.stream().map(Operator::toMongoInitialStateCriteria));
    }

    @Override
    public Map<String, Object> toMongoEventCriteria() {
        return pair("$and", operands.stream().map(Operator::toMongoEventCriteria));
    }

    @Override
    public Predicate<Map> toRuntimeCriteria() {
        //todo in case of empty operands list, should it return true or false by default?
        return operands.stream().map(Operator::toRuntimeCriteria).reduce(Predicate::and).orElse(o -> true);
    }
}
