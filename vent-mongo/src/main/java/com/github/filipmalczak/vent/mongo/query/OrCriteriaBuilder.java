package com.github.filipmalczak.vent.mongo.query;

import com.github.filipmalczak.vent.mongo.query.operator.Operator;
import com.github.filipmalczak.vent.mongo.query.operator.OrOperator;

public class OrCriteriaBuilder extends AbstractCriteriaBuilder{
    @Override
    public Operator toOperator() {
        return unpack(
            OrOperator::getOperands,
            OrOperator.builder().
                operands(
                    pullUp(OrOperator.class, OrOperator::getOperands, operators)
                ).
                build()
        );
    }
}
