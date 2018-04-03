package com.github.filipmalczak.vent.web.request;

import com.github.filipmalczak.vent.dto.Operation;
import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RawVentRequest {
    private String objectId;
    private String operation; //fixme looks ugly, model in request
    private Map payload;
}
