package com.github.filipmalczak.vent.dto;

import lombok.*;
import org.bson.types.ObjectId;

import java.util.Map;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class VentRequest {
    private final ObjectId objectId;
    private @NonNull final Operation operation;
    private @NonNull final Map payload;
}
