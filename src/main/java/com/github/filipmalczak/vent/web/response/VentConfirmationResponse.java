package com.github.filipmalczak.vent.web.response;

import com.github.filipmalczak.vent.dto.Operation;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VentConfirmationResponse {
    private String objectId;
    private String operation;
    private String timestamp;
}
