package com.github.filipmalczak.vent.mongo.query;

import com.github.filipmalczak.vent.TestConfiguration;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.mongo.ReactiveMongoVentFactory;
import com.github.filipmalczak.vent.mongo.MongoVentWithSpringDataConfiguration;
import com.github.filipmalczak.vent.tck.query.VentQueryTck;
import com.github.filipmalczak.vent.testing.TestingTemporalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig({TestConfiguration.class, MongoVentWithSpringDataConfiguration.class})
public class VentQueryTest extends VentQueryTck {
    @Autowired
    private ReactiveMongoTemplate operations;

    @Autowired
    private TestingTemporalService temporalService;

    @Override
    protected ReactiveVentDb provideClient() {
        ReactiveMongoVentFactory factory = new ReactiveMongoVentFactory();
        factory.
            temporalService(() -> temporalService).
            reactiveMongoOperations(() -> operations);
        return factory.newInstance();
    }
}