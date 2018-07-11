package com.github.filipmalczak.vent.embedded;

import com.github.filipmalczak.vent.VentSpringTest;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.tck.VentDbTck;
import com.github.filipmalczak.vent.testing.TestingTemporalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

//todo: test DeleteValue
//todo: test Delete
//@VentSpringTest
@Slf4j
@SpringJUnitConfig(EmbeddedWithSpringDataConfiguration.class)
//@SpringBootTest
class EmbeddedReactiveVentDbTest extends VentDbTck  {
    @Autowired
//    private EmbeddedReactiveVentDb ventDb;
//    private ReactiveMongoOperations operations;
    private ReactiveMongoTemplate operations;

    @Autowired
    private TestingTemporalService temporalService;

    @Override
    protected ReactiveVentDb provideClient() {
        EmbeddedReactiveVentFactory factory = new EmbeddedReactiveVentFactory();
//        TestingTemporalService temporalService = new TestingTemporalService();
        factory.
            temporalService(() -> temporalService).
            reactiveMongoOperations(() -> operations);
        return factory.newInstance();
    }
}