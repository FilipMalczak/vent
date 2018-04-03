package com.github.filipmalczak.vent.web.response;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class VentViewResponse implements Response {
    private Map object;
    private String ventedOn;
}
