package com.github.filipmalczak.vent.embedded.query;

import com.github.filipmalczak.vent.api.query.CriteriaBuilder;
import com.github.filipmalczak.vent.embedded.query.operator.EqualsOperator;
import com.github.filipmalczak.vent.embedded.query.operator.Operator;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractCriteriaBuilder implements CriteriaBuilder {
    protected List<Operator> operators = new LinkedList<>();

    @Override
    public CriteriaBuilder or(Consumer<CriteriaBuilder> orScope) {
        OrCriteriaBuilder builder = new OrCriteriaBuilder();
        orScope.accept(builder);
        operators.add(builder.toOperator());
        return this;
    }

    @Override
    public CriteriaBuilder not(Consumer<CriteriaBuilder> notScope) {
        NotCriteriaBuilder builder = new NotCriteriaBuilder();
        notScope.accept(builder);
        operators.add(builder.toOperator());
        return this;
    }

    @Override
    public CriteriaBuilder equals(String path, Object value) {
        operators.add(EqualsOperator.with(path, value));
        return this;
    }

    protected  <O extends Operator> List<Operator> pullUp(Class<O> toPullUp, Function<O, List<Operator>> extractor, List<Operator> collected){
        while (collected.stream().anyMatch(toPullUp::isInstance))
            collected = collected.stream().
                flatMap(o ->
                    toPullUp.isInstance(o) ?
                        extractor.apply((O) o).stream() :
                        Stream.of(o)
                ).
                collect(Collectors.toList());
        return collected;
    }

    protected  <O extends Operator> Operator unpack(Function<O, List<Operator>> extractor, O composite){
        List<Operator> children = extractor.apply(composite);
        if (children.size() == 1)
            return children.get(0);
        return composite;
    }

}
