package com.github.filipmalczak.ventrello.impl;

import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.github.filipmalczak.vent.helper.Struct.map;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class InitializeVent implements InitializingBean {
    private final ReactiveVentDb<?, ?, ?> reactiveVentDb;

    private final CountersManager countersManager;

    private final Optional<InitializeMockData> initializeMockData;

    @Override
    public void afterPropertiesSet() throws Exception {
        manage("tasks").
        then(manage("boards")).
        then(countersManager.prepareCounters()).
        then(Mono.justOrEmpty(initializeMockData).flatMap(i -> i.initialize())).
        subscribe();
    }

    private Mono<?> manage(String name){
        return reactiveVentDb.manage(name).map(
            s -> {
                if (s == Success.NO_OP_SUCCESS)
                    log.info("Collection '{}' already managed", name);
                else
                    log.info("Collection '{}' successfully managed", name);
                return s;
            }
        );
    }


}
