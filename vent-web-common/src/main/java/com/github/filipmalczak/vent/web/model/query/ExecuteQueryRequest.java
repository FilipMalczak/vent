package com.github.filipmalczak.vent.web.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteQueryRequest {
    private Operation operation;
    private QueryNode rootNode;
}
