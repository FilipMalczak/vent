package com.github.filipmalczak.vent.web.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class VentConfirmationResponse {
    private String objectId;
    private String operation;
    private String timestamp;
}
