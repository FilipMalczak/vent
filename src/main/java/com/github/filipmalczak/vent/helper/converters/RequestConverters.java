package com.github.filipmalczak.vent.helper.converters;

import com.github.filipmalczak.vent.dto.Operation;
import com.github.filipmalczak.vent.dto.VentRequest;
import com.github.filipmalczak.vent.web.request.RawVentRequest;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Optional;

@Component
public class RequestConverters {
    public Mono<VentRequest> convert(RawVentRequest request){
        return Mono.just(
            VentRequest.builder().
                objectId(Optional.ofNullable(request.getObjectId()).map(ObjectId::new).orElse(null)).
                operation(Operation.valueOf(request.getOperation())).
                payload(Optional.ofNullable(request.getPayload()).orElse(new HashMap())).
                build()
        );
    }
}
