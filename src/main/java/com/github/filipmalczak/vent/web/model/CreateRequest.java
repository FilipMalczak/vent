package com.github.filipmalczak.vent.web.model;

import lombok.Data;

import java.util.Map;

@Data
public class CreateRequest {
    private Map initialState;
}
