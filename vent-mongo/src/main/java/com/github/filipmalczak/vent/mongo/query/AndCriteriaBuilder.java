package com.github.filipmalczak.vent.mongo.query;

import com.github.filipmalczak.vent.mongo.query.operator.AndOperator;
import com.github.filipmalczak.vent.mongo.query.operator.Operator;

public class AndCriteriaBuilder extends AbstractCriteriaBuilder{
    @Override
    public Operator toOperator() {
        return unpack(
            AndOperator::getOperands,
            AndOperator.builder().
                operands(
                    pullUp(AndOperator.class, AndOperator::getOperands, operators)
                ).
                build()
        );
    }
}
