package com.github.filipmalczak.vent.api.blocking.query;

import com.github.filipmalczak.vent.api.general.query.QueryBuilder;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveQueryBuilder;
import com.github.filipmalczak.vent.traits.Blocking;

import java.util.stream.Stream;


public interface BlockingQueryBuilder
    <This extends BlockingQueryBuilder<This, QueryImpl>, QueryImpl extends BlockingVentQuery>
    extends
    QueryBuilder<This, QueryImpl, Stream<ObjectSnapshot>, Long, Boolean>, Blocking<ReactiveQueryBuilder<?, ?>> {
}
