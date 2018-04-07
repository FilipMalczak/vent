package com.github.filipmalczak.vent.helper.converters;

import com.github.filipmalczak.vent.dto.VentConfirmation;
import com.github.filipmalczak.vent.dto.VentedObject;
import com.github.filipmalczak.vent.web.response.VentConfirmationResponse;
import com.github.filipmalczak.vent.web.response.VentedObjectViewResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.just;

@Component
public class ResponseConverters {
    @Autowired
    private DateConverter dateConverter;

    public Mono<VentConfirmationResponse> convert(VentConfirmation confirmation){
        return just(
            VentConfirmationResponse.builder().
                objectId(confirmation.getObjectId().toHexString()).
                operation(confirmation.getOperation().toString()).
                timestamp(dateConverter.convert(confirmation.getTimestamp())).
                build()
        );
    }

    public Mono<VentedObjectViewResponse> convert(VentedObject ventedObject){
        return just(
            VentedObjectViewResponse.builder().
                object(ventedObject.getObject()).
                ventedOn(dateConverter.convert(ventedObject.getTimestamp())).
                build()
        );
    }
}
