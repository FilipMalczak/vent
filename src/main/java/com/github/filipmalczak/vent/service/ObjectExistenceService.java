package com.github.filipmalczak.vent.service;

import com.github.filipmalczak.vent.model.VentObject;
import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ObjectExistenceService {
    Mono<ObjectId> create(Mono<Map> initialValue);

    Mono<Void> delete(Mono<ObjectId> objectId);

    Mono<VentObject> find(Mono<ObjectId> objectId);
}
