package com.github.filipmalczak.vent.web.client.query;

import com.github.filipmalczak.vent.api.general.query.CriteriaBuilder;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveQueryBuilder;
import com.github.filipmalczak.vent.web.integration.Converters;
import com.github.filipmalczak.vent.web.model.query.NodeType;
import com.github.filipmalczak.vent.web.model.query.QueryNode;
import lombok.AllArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;

import static com.github.filipmalczak.vent.helper.Struct.list;

@AllArgsConstructor
public class WebQueryBuilder  implements ReactiveQueryBuilder<WebQueryBuilder, WebQuery>{
    private WebClient webClient;
    private String ventCollectionName;
    private WebCriteriaBuilder criteriaBuilder;
    private Converters converters;

    @Override
    public WebQueryBuilder and(Consumer<CriteriaBuilder> andScope) {
        criteriaBuilder.and(andScope);
        return this;
    }

    @Override
    public WebQueryBuilder or(Consumer<CriteriaBuilder> orScope) {
        criteriaBuilder.or(orScope);
        return this;
    }

    @Override
    public WebQueryBuilder not(Consumer<CriteriaBuilder> notScope) {
        criteriaBuilder.not(notScope);
        return this;
    }

    @Override
    public WebQueryBuilder equals(String path, Object value) {
        criteriaBuilder.equals(path, value);
        return this;
    }

    @Override
    public WebQuery build() {
        return new WebQuery(webClient, ventCollectionName, new QueryNode(NodeType.ROOT, criteriaBuilder.nodes, list()), converters);
    }
}
