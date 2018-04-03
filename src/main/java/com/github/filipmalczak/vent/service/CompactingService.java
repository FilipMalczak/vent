package com.github.filipmalczak.vent.service;

import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;

public interface CompactingService {
    Mono<Void> compact(Mono<ObjectId> objectId);
}
