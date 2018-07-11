package com.github.filipmalczak.vent.embedded.query;

import com.github.filipmalczak.vent.VentSpringTest;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.embedded.EmbeddedReactiveVentFactory;
import com.github.filipmalczak.vent.embedded.EmbeddedWithSpringDataConfiguration;
import com.github.filipmalczak.vent.tck.query.VentQueryTck;
import com.github.filipmalczak.vent.testing.TestingTemporalService;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(EmbeddedWithSpringDataConfiguration.class)
public class EmbeddedQueryTest extends VentQueryTck {
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