package com.github.filipmalczak.vent.service;

import com.github.filipmalczak.vent.dto.VentConfirmation;
import com.github.filipmalczak.vent.dto.VentRequest;
import reactor.core.publisher.Mono;

public interface VentHandlingService {
    Mono<VentConfirmation> handle(VentRequest request);
}
