package com.github.filipmalczak.vent.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class CreateRequest {
    private Map initialState;
}
