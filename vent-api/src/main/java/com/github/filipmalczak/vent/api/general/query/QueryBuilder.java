package com.github.filipmalczak.vent.api.general.query;



import java.time.LocalDateTime;
import java.util.function.Consumer;


public interface QueryBuilder<
        FindResult, CountResult, ExistsResult,
        This extends QueryBuilder<FindResult, CountResult, ExistsResult, This, QueryImpl>,
        QueryImpl extends VentQuery<FindResult, CountResult, ExistsResult>
    > extends CriteriaBuilder {

    This and(Consumer<CriteriaBuilder> andScope);

    This or(Consumer<CriteriaBuilder> orScope);
    This not(Consumer<CriteriaBuilder> notScope);
    This equals(String path, Object value);

    QueryImpl build();

    default FindResult find(LocalDateTime queryAt){
        return build().find(queryAt);
    }

    default CountResult count(LocalDateTime queryAt){
        return build().count(queryAt);
    }

    default ExistsResult exists(LocalDateTime queryAt){
        return build().exists(queryAt);
    }
}
