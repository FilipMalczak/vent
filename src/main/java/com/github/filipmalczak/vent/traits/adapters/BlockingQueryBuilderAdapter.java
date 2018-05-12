package com.github.filipmalczak.vent.traits.adapters;

import com.github.filipmalczak.vent.api.blocking.BlockingVentQuery;
import com.github.filipmalczak.vent.api.query.BlockingQueryBuilder;
import com.github.filipmalczak.vent.api.query.CriteriaBuilder;
import com.github.filipmalczak.vent.api.query.ReactiveQueryBuilder;
import com.github.filipmalczak.vent.embedded.query.operator.Operator;
import lombok.Value;

import java.util.function.Consumer;

@Value
class BlockingQueryBuilderAdapter implements BlockingQueryBuilder<BlockingQueryBuilderAdapter, BlockingVentQuery> {
    private ReactiveQueryBuilder<?, ?> builder;

    @Override
    public BlockingQueryBuilderAdapter and(Consumer<CriteriaBuilder> andScope) {
        builder.and(andScope);
        return this;
    }

    @Override
    public BlockingQueryBuilderAdapter or(Consumer<CriteriaBuilder> orScope) {
        builder.or(orScope);
        return this;
    }

    @Override
    public BlockingQueryBuilderAdapter not(Consumer<CriteriaBuilder> notScope) {
        builder.not(notScope);
        return this;
    }

    @Override
    public BlockingQueryBuilderAdapter equals(String path, Object value) {
        builder.equals(path, value);
        return this;
    }

    @Override
    public Operator toOperator() {
        return builder.toOperator();
    }

    @Override
    public BlockingVentQuery build() {
        return builder.build().asBlocking();
    }

    @Override
    public ReactiveQueryBuilder<?, ?> asReactive() {
        return builder;
    }
}
