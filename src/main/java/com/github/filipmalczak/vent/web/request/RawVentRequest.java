package com.github.filipmalczak.vent.web.request;

import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class RawVentRequest {
    private String objectId;
    private String operation; //fixme looks ugly, model in request
    private Map payload;
}
