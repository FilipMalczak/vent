package com.github.filipmalczak.vent.api.general.query;

import java.util.function.Consumer;


public interface CriteriaBuilder {
    default CriteriaBuilder and(Consumer<CriteriaBuilder> andScope){
        andScope.accept(this);
        return this;
    }

    CriteriaBuilder or(Consumer<CriteriaBuilder> orScope);
    CriteriaBuilder not(Consumer<CriteriaBuilder> notScope);
    CriteriaBuilder equals(String path, Object value);

}
