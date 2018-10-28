package com.github.filipmalczak.vent.mongo.extension.scan.model;

import lombok.Value;

import java.time.Duration;

@Value
public class CollectionRepresentation {
    String collectionName;
    int previousPeriodsCount;
    Duration currentPeriodDuration;
    Duration collectionDuration;
}
