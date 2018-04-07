package com.github.filipmalczak.vent.dto;

import lombok.Value;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Value(staticConstructor = "of")
public class VentConfirmation {
    private ObjectId objectId;
    private Operation operation;
    private LocalDateTime timestamp;
}
