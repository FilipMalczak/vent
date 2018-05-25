package com.github.filipmalczak.vent.adapter.impl.delegates;

import com.github.filipmalczak.vent.api.blocking.query.BlockingQueryBuilder;
import com.github.filipmalczak.vent.api.blocking.query.BlockingVentQuery;
import com.github.filipmalczak.vent.api.general.query.CriteriaBuilder;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveQueryBuilder;
import lombok.Value;

import java.util.function.Consumer;

@Value
public class BlockingQueryBuilderAdapter implements BlockingQueryBuilder<BlockingQueryBuilderAdapter, BlockingVentQuery> {
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
    public BlockingVentQuery build() {
        return new BlockingQueryAdapter(builder.build());
    }
}
