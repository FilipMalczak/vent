package com.github.filipmalczak.ventrello.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class CountersController {
    private final CountersManager countersManager;

    @PostMapping(value = "/counters/{name}/increment", produces = "application/json;charset=utf-8")
    public Mono<Integer> incrementCounter(@PathVariable String name){
        return countersManager.increment(name);
    }
}
