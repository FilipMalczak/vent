package com.github.filipmalczak.vent.embedded;

import com.github.filipmalczak.vent.TestConfiguration;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.tck.VentDbTck;
import com.github.filipmalczak.vent.testing.TestingTemporalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

//todo: test DeleteValue
//todo: test Delete
@Slf4j
@SpringJUnitConfig({TestConfiguration.class, EmbeddedWithSpringDataConfiguration.class})
class EmbeddedReactiveVentDbTest extends VentDbTck  {
    @Autowired
    private ReactiveMongoTemplate operations;

    @Autowired
    private TestingTemporalService temporalService;

    @Override
    protected ReactiveVentDb provideClient() {
        EmbeddedReactiveVentFactory factory = new EmbeddedReactiveVentFactory();
        factory.
            temporalService(() -> temporalService).
            reactiveMongoOperations(() -> operations);
        return factory.newInstance();
    }
}