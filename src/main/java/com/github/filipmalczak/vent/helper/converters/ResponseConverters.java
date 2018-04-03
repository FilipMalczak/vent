package com.github.filipmalczak.vent.helper.converters;

import com.github.filipmalczak.vent.dto.VentConfirmation;
import com.github.filipmalczak.vent.web.response.VentConfirmationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ResponseConverters {
    @Autowired
    private DateConverter dateConverter;

    public Mono<VentConfirmationResponse> convert(VentConfirmation confirmation){
        return Mono.just(
            VentConfirmationResponse.builder().
                objectId(confirmation.getObjectId().toHexString()).
                operation(confirmation.getOperation().toString()).
                timestamp(dateConverter.convert(confirmation.getTimestamp())).
                build()
        );
    }
}
