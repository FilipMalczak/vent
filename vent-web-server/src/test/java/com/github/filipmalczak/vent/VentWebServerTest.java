package com.github.filipmalczak.vent;

import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.tck.VentDbTck;
import com.github.filipmalczak.vent.testing.TestingTemporalService;
import com.github.filipmalczak.vent.web.client.ReactiveWebVentDbClient;
import com.github.filipmalczak.vent.web.integration.Converters;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import static com.github.filipmalczak.vent.web.paths.CommonPaths.COLLECTIONS;

//@Disabled
@ExtendWith(SpringExtension.class)
//@WebFluxTest//(VentWebServer.class)

//@Import(VentWebServer.class)
@TestPropertySource(properties = "local.server.port=8080")
class VentWebServerTest extends VentDbTck {
//    @LocalServerPort()
    protected static int port;

    protected static ConfigurableApplicationContext configurableApplicationContext;

    @BeforeAll
    public static void setServerUp(){
        configurableApplicationContext = SpringApplication.run(new Class[]{TestServerConfig.class, VentWebServer.class}, new String[0]);
        port = Integer.valueOf(((ConfigurableApplicationContext) configurableApplicationContext).getEnvironment().getProperty("local.server.port"));
//        configurableApplicationContext.start();
    }

    @AfterAll
    public static void tearServerDown(){
        configurableApplicationContext.stop();
    }
//    @Autowired
//    private ReactiveVentDb reactiveVentDb;
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
        return new ReactiveWebVentDbClient(WebClient.create("http://localhost:"+port), new Converters());
    }

    @Override
    protected TestingTemporalService provideTestingTemporalService() {
//        return new TestingTemporalService();
        return (TestingTemporalService) configurableApplicationContext.getBean("temporalService");
    }
}