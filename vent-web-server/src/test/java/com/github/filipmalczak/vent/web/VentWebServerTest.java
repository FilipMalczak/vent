package com.github.filipmalczak.vent.web;

import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.web.orchestration.TestServerManager;
import com.github.filipmalczak.vent.web.orchestration.WithServerTrait;
import com.github.filipmalczak.vent.tck.VentDbTck;
import com.github.filipmalczak.vent.testing.TestingTemporalService;
import com.github.filipmalczak.vent.web.client.ReactiveWebVentDbClient;
import com.github.filipmalczak.vent.web.integration.Converters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "local.server.port=8080")
class VentWebServerTest extends VentDbTck implements WithServerTrait {

    @Test
    public void startsUp(){
        //no-op, just to check whether webapp starts up at all
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