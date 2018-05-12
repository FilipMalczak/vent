package com.github.filipmalczak.vent.api.query;

import com.github.filipmalczak.vent.api.ObjectSnapshot;
import com.github.filipmalczak.vent.api.blocking.BlockingVentQuery;
import com.github.filipmalczak.vent.traits.Blocking;

import java.util.stream.Stream;

public interface BlockingQueryBuilder
    <This extends BlockingQueryBuilder<This, QueryImpl>, QueryImpl extends BlockingVentQuery>
    extends
    QueryBuilder<This, QueryImpl, Stream<ObjectSnapshot>, Long, Boolean>, Blocking<ReactiveQueryBuilder<?, ?>> {
}
