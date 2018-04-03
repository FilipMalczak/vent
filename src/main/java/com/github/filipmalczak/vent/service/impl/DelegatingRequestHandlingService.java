package com.github.filipmalczak.vent.service.impl;

import com.github.filipmalczak.vent.dto.*;
import com.github.filipmalczak.vent.repository.Objects;
import com.github.filipmalczak.vent.service.ObjectExistenceService;
import com.github.filipmalczak.vent.service.TimestampService;
import com.github.filipmalczak.vent.service.VentingService;
import com.github.filipmalczak.vent.service.VentRequestHandlingService;
import org.apache.commons.lang3.NotImplementedException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static reactor.core.publisher.Mono.fromCallable;
import static reactor.core.publisher.Mono.just;

@Service
public class DelegatingRequestHandlingService implements VentRequestHandlingService {
    @Autowired
    private TimestampService timestampService;

    @Autowired
    private Objects objects;

    @Autowired
    private ObjectExistenceService objectExistenceService;

    @Autowired
    private VentingService ventingService;

    @Override
    public Mono<OperationResult> handle(VentRequest request) {
        switch(request.getOperation()){
            case CREATE: return handleCreate(request);
            case GET: return handleGet(request);
            case DELETE: return handleDelete(request);
            default: throw new NotImplementedException("Operation "+request.getOperation()+" is not ready yet!");
        }
    }

    private Mono<OperationResult> handleCreate(VentRequest request){
        return objectExistenceService.
            create(
                fromCallable(request::getObjectId),
                fromCallable(request::getPayload).
                    map(m -> (Map) m.getOrDefault("initialValue", new HashMap<>()))
            ).
            flatMap( id -> confirm(id, request.getOperation()));
    }

    private Mono<OperationResult> handleDelete(VentRequest request){
        return objectExistenceService.
            delete(fromCallable(request::getObjectId)).
            then( confirmRequest(request) );
    }

    private Mono<OperationResult> handleGet(VentRequest request){
        //todo add support for custom schema
        return ventingService.applyVents(
                objectExistenceService.find(fromCallable(request::getObjectId))
            ).
            map( m -> VentedObject.builder().
                object(m).
                timestamp(timestampService.now()).
                build()
            );
    }

    private Mono<OperationResult> confirmRequest(VentRequest request){
        return confirm(request.getObjectId(), request.getOperation());
    }

    private Mono<OperationResult> confirm(ObjectId objectId, Operation operation){
        return just(VentConfirmation.of(objectId, operation, timestampService.now()));
    }
}
