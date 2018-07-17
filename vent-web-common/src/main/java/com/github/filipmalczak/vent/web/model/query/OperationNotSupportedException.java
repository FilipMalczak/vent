package com.github.filipmalczak.vent.web.model.query;

import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;

@AllArgsConstructor
@ToString
public class OperationNotSupportedException extends RuntimeException {
    private Operation operation;
}
