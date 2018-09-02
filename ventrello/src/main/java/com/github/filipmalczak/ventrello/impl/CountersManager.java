package com.github.filipmalczak.ventrello.impl;

import com.github.filipmalczak.vent.api.model.EventConfirmation;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.filipmalczak.vent.helper.Struct.map;
import static com.github.filipmalczak.vent.helper.Struct.pair;
import static reactor.core.publisher.Mono.just;

@Service
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class CountersManager {
    private final ReactiveVentDb<?, ?, ?> reactiveVentDb;

    @Autowired
    public CountersManager(ReactiveVentDb<?, ?, ?> reactiveVentDb) {
        this.reactiveVentDb = reactiveVentDb;
    }

    private Map<String, IdAndLock> ids = new ConcurrentHashMap<>();

    @Value
    static class IdAndLock {
        VentId id;
        Lock lock;
    }

    public Mono<Success> prepareCounters(){
        return reactiveVentDb.manage("counters").
            flatMap(s ->
                s == Success.NO_OP_SUCCESS ?
                    just(s) :
                    createCounter("tasks").
                        then(createCounter("boards")).
                        then(just(Success.SUCCESS))
            );
    }

    private Mono<Success> createCounter(String name){
        return reactiveVentDb.getCollection("counters").
            create(map(
                pair("name", name),
                pair("value", 0)
            )).map(id -> {
                ids.put(name, new IdAndLock(id, new ReentrantLock()));
                return Success.SUCCESS;
            });
    }

    public Mono<Integer> get(String name){
        IdAndLock idAndLock = ids.get(name);
        synchronized (idAndLock.lock) {
            return reactiveVentDb.
                getCollection("counters").
                get(idAndLock.id).
                map(state -> (int) state.getState().get("value"));
        }
    }

    public Mono<Integer> set(String name, int val){
        IdAndLock idAndLock = ids.get(name);
        synchronized (idAndLock.lock) {
            return reactiveVentDb.
                getCollection("counters").
                putValue(idAndLock.id, "value", val).
                then(just(val));
        }
    }

    public Mono<Integer> increment(String name){
        return get(name).flatMap(v ->
            set(name, v+1)
        );
    }
}
