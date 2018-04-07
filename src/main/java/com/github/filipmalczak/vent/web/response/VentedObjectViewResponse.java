package com.github.filipmalczak.vent.web.response;

import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class VentedObjectViewResponse {
    private Map object;
    private String ventedOn;
}
