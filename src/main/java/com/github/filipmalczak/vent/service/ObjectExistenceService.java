package com.github.filipmalczak.vent.service;

import com.github.filipmalczak.vent.model.VentObject;
import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public interface ObjectExistenceService {
    Mono<ObjectId> create(Mono<ObjectId> objectId, Mono<Map> initialValue);
//
//    default Mono<ObjectId> create(Map initialValue){
//        return create(null, initialValue);
//    }
//
//    default Mono<ObjectId> create(ObjectId objectId){
//        return create(objectId, new HashMap());
//    }
//
//    default Mono<ObjectId> create(){
//        return create(null, new HashMap());
//    }

    Mono<Void> delete(Mono<ObjectId> objectId);

    Mono<VentObject> find(Mono<ObjectId> objectId);
}
