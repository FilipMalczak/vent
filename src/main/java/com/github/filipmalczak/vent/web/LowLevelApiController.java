package com.github.filipmalczak.vent.web;

import com.github.filipmalczak.vent.helper.converters.ResponseConverters;
import com.github.filipmalczak.vent.helper.converters.RequestConverters;
import com.github.filipmalczak.vent.service.VentRequestHandlingService;
import com.github.filipmalczak.vent.web.request.RawVentRequest;
import com.github.filipmalczak.vent.web.response.Response;
import com.github.filipmalczak.vent.web.response.VentConfirmationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class LowLevelApiController {
    @Autowired
    private VentRequestHandlingService ventRequestHandlingService;

    @Autowired
    private RequestConverters requestConverters;

    @Autowired
    private ResponseConverters responseConverters;

    @PostMapping("/vent/raw")
    public Mono<Response> rawVent(@RequestBody Mono<RawVentRequest> request){
        return request.
            flatMap(requestConverters::convert).
            flatMap(ventRequestHandlingService::handle).
            flatMap(responseConverters::convert);
    }
}
