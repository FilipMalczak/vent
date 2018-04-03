package com.github.filipmalczak.vent.service.impl;

import com.github.filipmalczak.vent.dto.Operation;
import com.github.filipmalczak.vent.dto.VentConfirmation;
import com.github.filipmalczak.vent.dto.VentRequest;
import com.github.filipmalczak.vent.model.Vent;
import com.github.filipmalczak.vent.model.VentObject;
import com.github.filipmalczak.vent.repository.Objects;
import com.github.filipmalczak.vent.service.TimestampService;
import com.github.filipmalczak.vent.service.VentHandlingService;
import org.apache.commons.lang3.NotImplementedException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CoreVentHandlingService implements VentHandlingService {
    @Autowired
    private TimestampService timestampService;

    @Autowired
    private Objects objects;

    @Override
    public Mono<VentConfirmation> handle(VentRequest request) {
        switch(request.getOperation()){
            case CREATE: return handleCreate(request);
            default: throw new NotImplementedException("Operation "+request.getOperation()+" is not ready yet!");
        }
    }

    private Mono<VentConfirmation> handleCreate(VentRequest request){
        Vent vent = new Vent(Operation.CREATE, request.getPayload(), timestampService.now());
        VentObject newObject = VentObject.builder().
            objectId(request.getObjectId()).
            event(vent).
            lastCompacted(vent.getTimestamp()).
            build();
        return objects.
            save(newObject).
            flatMap(o ->
                confirm(o.getObjectId(), request.getOperation())
            );
    }

    private Mono<VentConfirmation> confirm(VentRequest request){
        return confirm(request.getObjectId(), request.getOperation());
    }

    private Mono<VentConfirmation> confirm(ObjectId objectId, Operation operation){
        return Mono.just(VentConfirmation.of(objectId, operation, timestampService.now()));
    }
}
