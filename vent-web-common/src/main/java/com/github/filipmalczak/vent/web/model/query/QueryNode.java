package com.github.filipmalczak.vent.web.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryNode {
    private NodeType nodeType;
    private List<QueryNode> children;
    private List payload;
}
