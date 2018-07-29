package com.github.filipmalczak.vent.mongo.query;

import com.github.filipmalczak.vent.mongo.query.operator.NotOperator;
import com.github.filipmalczak.vent.mongo.query.operator.Operator;

public class NotCriteriaBuilder extends AndCriteriaBuilder {
    @Override
    public Operator toOperator() {
        Operator child = super.toOperator();
        if (child instanceof NotOperator)
            return ((NotOperator) child).getNegated();
        return NotOperator.of(super.toOperator());
    }
}
