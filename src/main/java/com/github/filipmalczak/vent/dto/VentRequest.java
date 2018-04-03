package com.github.filipmalczak.vent.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.bson.types.ObjectId;

import java.util.Map;

@Builder
@Getter
public class VentRequest {
    private final ObjectId objectId;
    private @NonNull final Operation operation;
    private @NonNull final Map payload;
}
