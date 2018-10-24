package com.github.filipmalczak.vent.mongo.extension.scan.model;

import lombok.Value;
import org.bson.types.ObjectId;

import java.time.Duration;
import java.time.LocalDateTime;

@Value
public class PageRepresentation {
    private final ObjectId objectId;
    private final ObjectId pageId;
    private final boolean isFirstPage;
    private final LocalDateTime startingFrom;
    private final Duration timeSpan;
    private final boolean hasInitialState;
    private final long eventCount;
}
