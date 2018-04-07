package com.github.filipmalczak.vent.web;

import com.github.filipmalczak.vent.dto.VentedObject;
import com.github.filipmalczak.vent.helper.converters.RequestConverters;
import com.github.filipmalczak.vent.helper.converters.ResponseConverters;
import com.github.filipmalczak.vent.repository.Objects;
import com.github.filipmalczak.vent.service.TimestampService;
import com.github.filipmalczak.vent.service.VentRequestHandlingService;
import com.github.filipmalczak.vent.service.VentingService;
import com.github.filipmalczak.vent.web.request.RawVentRequest;
import com.github.filipmalczak.vent.web.response.VentConfirmationResponse;
import com.github.filipmalczak.vent.web.response.VentedObjectViewResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.just;

@RestController
public class LowLevelApiController {
    @Autowired
    private VentRequestHandlingService ventRequestHandlingService;

    @Autowired
    private RequestConverters requestConverters;

    @Autowired
    private ResponseConverters responseConverters;

    @Autowired
    private Objects objects;

    @Autowired
    private VentingService ventingService;

    @Autowired
    private TimestampService timestampService;

    @PostMapping("/vent/raw")
    public Mono<VentConfirmationResponse> rawVent(@RequestBody Mono<RawVentRequest> request){
        return request.
            flatMap(requestConverters::convert).
            log().
            flatMap(ventRequestHandlingService::handle).
            log().
            flatMap(responseConverters::convert);
    }

    @GetMapping("/object/{id}")
    public Mono<VentedObjectViewResponse> getObject(@PathVariable("id") String objectId){
        return objects.findById(new ObjectId(objectId)).
            //todo change signature, to avoid this idiotic just(o)
            flatMap(o -> ventingService.applyVents(just(o))).
            map(m -> VentedObject.builder().
                object(m).
                timestamp(timestampService.now()).
                build()
            ).
            flatMap(responseConverters::convert);
    }
}
