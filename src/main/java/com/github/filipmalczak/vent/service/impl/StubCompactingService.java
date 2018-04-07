package com.github.filipmalczak.vent.service.impl;

import com.github.filipmalczak.vent.service.CompactingService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class StubCompactingService implements CompactingService{
    @Override
    public Mono<Void> compact(Mono<ObjectId> objectId) {
        return Mono.just(null);
    }
}
