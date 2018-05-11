package com.github.filipmalczak.vent.api.query;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public interface QueryBuilder
    <This extends QueryBuilder<This, QueryImpl, FindResult, CountResult, ExistsResult>,
        QueryImpl extends VentQuery<FindResult, CountResult, ExistsResult>,
        FindResult, CountResult, ExistsResult> extends CriteriaBuilder {

    This and(Consumer<CriteriaBuilder> andScope);

    This or(Consumer<CriteriaBuilder> orScope);
    This not(Consumer<CriteriaBuilder> notScope);
    This equals(String path, Object value);

    QueryImpl build();

    default FindResult execute(LocalDateTime queryAt){
        return build().find(queryAt);
    }
}
