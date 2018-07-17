package com.github.filipmalczak.vent.web.client;

import com.github.filipmalczak.vent.api.general.query.CriteriaBuilder;
import com.github.filipmalczak.vent.helper.Struct;
import com.github.filipmalczak.vent.web.model.query.NodeType;
import com.github.filipmalczak.vent.web.model.query.QueryNode;

import java.util.List;
import java.util.function.Consumer;

import static com.github.filipmalczak.vent.helper.Struct.list;

public class WebCriteriaBuilder implements CriteriaBuilder {
    protected List<QueryNode> nodes = list();

    @Override
    public CriteriaBuilder and(Consumer<CriteriaBuilder> andScope) {
        return addNode(NodeType.AND, andScope, list());
    }

    @Override
    public WebCriteriaBuilder or(Consumer<CriteriaBuilder> orScope) {
        return addNode(NodeType.OR, orScope, list());
    }

    @Override
    public WebCriteriaBuilder not(Consumer<CriteriaBuilder> notScope) {
        return addNode(NodeType.NOT, notScope, list());
    }

    @Override
    public WebCriteriaBuilder equals(String path, Object value) {
        return addNode(NodeType.EQUALS, cb -> {/*no-op*/}, list(path, value));
    }

    //functionally second arg is a Consumer, but its role here is being a producer. Functional can be fun.
    private WebCriteriaBuilder addNode(NodeType type, Consumer<CriteriaBuilder> childrenProducer, List payload){
        WebCriteriaBuilder childrenBuilder = new WebCriteriaBuilder();
        childrenProducer.accept(childrenBuilder);
        nodes.add(new QueryNode(type, childrenBuilder.nodes, payload));
        return this;
    }
}
