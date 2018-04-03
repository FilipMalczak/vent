package com.github.filipmalczak.vent.helper.converters;

import com.github.filipmalczak.vent.dto.VentedObject;
import com.github.filipmalczak.vent.dto.OperationResult;
import com.github.filipmalczak.vent.dto.VentConfirmation;
import com.github.filipmalczak.vent.web.response.Response;
import com.github.filipmalczak.vent.web.response.VentConfirmationResponse;
import com.github.filipmalczak.vent.web.response.VentViewResponse;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.just;

@Component
public class ResponseConverters {
    @Autowired
    private DateConverter dateConverter;

    public Mono<Response> convert(OperationResult result){
        //fixme this is what's ugly with marker interfaces
        if (result instanceof VentConfirmation)
            return convert((VentConfirmation) result);
        if (result instanceof VentedObject)
            return convert((VentedObject) result);
        throw new NotImplementedException("Converter for operation result type "+result.getClass()+" not available!");
    }

    private Mono<Response> convert(VentConfirmation confirmation){
        return just(
            VentConfirmationResponse.builder().
                objectId(confirmation.getObjectId().toHexString()).
                operation(confirmation.getOperation().toString()).
                timestamp(dateConverter.convert(confirmation.getTimestamp())).
                build()
        );
    }

    private Mono<Response> convert(VentedObject ventedObject){
        return just(
            VentViewResponse.builder().
                object(ventedObject.getObject()).
                ventedOn(dateConverter.convert(ventedObject.getTimestamp())).
                build()
        );
    }
}
