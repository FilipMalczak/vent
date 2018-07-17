package com.github.filipmalczak.vent.web.client;

import com.github.filipmalczak.vent.TestServerManager;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.tck.query.VentQueryTck;
import com.github.filipmalczak.vent.testing.TestingTemporalService;
import com.github.filipmalczak.vent.web.integration.Converters;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "local.server.port=8080")
class WebQueryTest extends VentQueryTck {

    //todo: cover non-happy scenarios with tests; figure out exceptions to be thrown and when

    @BeforeAll
    public static void setServerUp(){
        TestServerManager.start();
    }

    @AfterAll
    public static void tearServerDown(){
        TestServerManager.start();
    }

    @Override
    protected ReactiveVentDb provideClient() {
        return new ReactiveWebVentDbClient(TestServerManager.newClient(), new Converters());
    }

    @Override
    protected TestingTemporalService provideTestingTemporalService() {
        return (TestingTemporalService) TestServerManager.getBean("temporalService");
    }
}