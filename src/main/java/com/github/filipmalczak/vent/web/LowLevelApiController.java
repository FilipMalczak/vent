package com.github.filipmalczak.vent.web;

import com.github.filipmalczak.vent.helper.converters.ResponseConverters;
import com.github.filipmalczak.vent.helper.converters.RequestConverters;
import com.github.filipmalczak.vent.service.VentHandlingService;
import com.github.filipmalczak.vent.web.request.RawVentRequest;
import com.github.filipmalczak.vent.web.response.VentConfirmationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class LowLevelApiController {
    @Autowired
    private VentHandlingService ventHandlingService;

    @Autowired
    private RequestConverters requestConverters;

    @Autowired
    private ResponseConverters responseConverters;

    @PostMapping("/vent/raw")
    public Mono<VentConfirmationResponse> rawVent(@RequestBody Mono<RawVentRequest> request){
        return request.
            flatMap(requestConverters::convert).
            flatMap(ventHandlingService::handle).
            flatMap(responseConverters::convert);
    }
}
