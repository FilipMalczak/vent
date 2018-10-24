package com.github.filipmalczak.vent.mongo;

import com.github.filipmalczak.vent.TestConfiguration;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.tck.VentDbTck;
import com.github.filipmalczak.vent.testing.TestingTemporalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static com.github.filipmalczak.vent.mongo.ReactiveMongoVentFactory.VentServiceCreators.*;

//todo: test DeleteValue
//todo: test Delete
@Slf4j
@SpringJUnitConfig({TestConfiguration.class, MongoVentWithSpringDataConfiguration.class})
class VentDbTest extends VentDbTck  {
    @Autowired
    private ReactiveMongoTemplate operations;

    @Autowired
    private TestingTemporalService temporalService;

    @Override
    protected ReactiveVentDb provideClient() {
        ReactiveMongoVentFactory factory = new ReactiveMongoVentFactory();
        return factory.create(configure()
            .temporalService(() -> temporalService)
            .reactiveMongoOperations(() -> operations)
            .build()
        );
    }
}