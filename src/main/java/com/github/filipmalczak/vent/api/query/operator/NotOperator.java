package com.github.filipmalczak.vent.api.query.operator;

import com.github.filipmalczak.vent.api.ObjectSnapshot;
import lombok.Value;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDateTime;
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
    public Predicate<ObjectSnapshot> toRuntimeCriteria() {
        return negated.toRuntimeCriteria().negate();
    }
}
