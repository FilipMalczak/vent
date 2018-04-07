package com.github.filipmalczak.vent.service.impl;

import com.github.filipmalczak.vent.dto.Operation;
import com.github.filipmalczak.vent.dto.VentConfirmation;
import com.github.filipmalczak.vent.dto.VentConfirmation;
import com.github.filipmalczak.vent.dto.VentRequest;
import com.github.filipmalczak.vent.repository.Objects;
import com.github.filipmalczak.vent.service.ObjectExistenceService;
import com.github.filipmalczak.vent.service.TimestampService;
import com.github.filipmalczak.vent.service.VentRequestHandlingService;
import com.github.filipmalczak.vent.service.VentingService;
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
    public Mono<VentConfirmation> handle(VentRequest request) {
        switch(request.getOperation()){
            case CREATE: return handleCreate(request);
            case DELETE: return handleDelete(request);
            default: throw new NotImplementedException("Operation "+request.getOperation()+" is not ready yet!");
        }
    }

    private Mono<VentConfirmation> handleCreate(VentRequest request){
        return objectExistenceService.
            create(
                fromCallable(request::getPayload).
                    map(m -> (Map) m.getOrDefault("initialValue", new HashMap<>()))
            ).
            log().
            flatMap( id -> confirm(id, request.getOperation()));
    }

    private Mono<VentConfirmation> handleDelete(VentRequest request){
        return objectExistenceService.
            delete(fromCallable(request::getObjectId)).
            then( confirmRequest(request) );
    }

    private Mono<VentConfirmation> confirmRequest(VentRequest request){
        return confirm(request.getObjectId(), request.getOperation());
    }

    private Mono<VentConfirmation> confirm(ObjectId objectId, Operation operation){
        return just(VentConfirmation.of(objectId, operation, timestampService.now()));
    }
}
