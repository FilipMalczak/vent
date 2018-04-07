package com.github.filipmalczak.vent.service;

import com.github.filipmalczak.vent.dto.VentConfirmation;
import com.github.filipmalczak.vent.dto.VentRequest;
import reactor.core.publisher.Mono;

public interface VentRequestHandlingService {
    Mono<VentConfirmation> handle(VentRequest request);
}
