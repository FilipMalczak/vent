package com.github.filipmalczak.vent;

import com.github.filipmalczak.vent.api.general.VentDb;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.tck.VentDbTck;
import com.github.filipmalczak.vent.testing.TestingTemporalService;
import com.github.filipmalczak.vent.web.client.ReactiveWebVentDbClient;
import com.github.filipmalczak.vent.web.controller.CollectionController;
import com.github.filipmalczak.vent.web.controller.DbController;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import static com.github.filipmalczak.vent.web.paths.CommonPaths.COLLECTIONS;
import static org.junit.jupiter.api.Assertions.*;
import static reactor.core.publisher.Mono.just;

@Disabled
@ExtendWith(SpringExtension.class)
@WebFluxTest
@Import(VentWebServer.class)
@TestPropertySource(properties = "local.server.port=8083")
class VentWebServerTest extends VentDbTck {
    @LocalServerPort()
    private int port;

//    @Autowired
//    private MapperFacade mapperFacade;

    @Test
    public void startsUp(){
        System.out.println();
        Flux.just("START").concatWith(
            WebClient.
                create("http://localhost:"+port).
                head().
                uri(COLLECTIONS).
                exchange().
                flux().
                flatMap(r -> r.bodyToFlux(String.class))
        ).concatWith(
            Flux.just("STOP")
        ).log().toStream().forEach(System.out::println);
        System.out.println();
    }

    @Override
    protected ReactiveVentDb provideClient() {
        return new ReactiveWebVentDbClient(WebClient.create("http://localhost:"+port), null);
    }

    @Override
    protected TestingTemporalService provideTestingTemporalService() {
        return new TestingTemporalService();
    }
}