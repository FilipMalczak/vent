package com.github.filipmalczak.vent.web.model.query;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class OperationNotSupportedException extends RuntimeException {
    private Operation operation;
}
