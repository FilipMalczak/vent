package com.github.filipmalczak.vent.web.client;

import com.github.filipmalczak.vent.TestConfiguration;
import com.github.filipmalczak.vent.VentWebServer;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.tck.VentDbTck;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

@Disabled
@ExtendWith(SpringExtension.class)
@WebFluxTest
//@ComponentScan(basePackageClasses = {VentWebServer.class, TestConfiguration.class, CommonWebConfiguration.class})
//@ImportAutoConfiguration(MongoConfigurationSupport.class)
@TestPropertySource(properties = "server.port="+ReactiveWebVentDbClientTest.TEST_PORT)
public class ReactiveWebVentDbClientTest extends VentDbTck {
    public static final int TEST_PORT = 12345;
//    @LocalServerPort
//    private int port;
    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected ReactiveVentDb provideClient() {
        return new ReactiveWebVentDbClient(WebClient.builder().baseUrl("localhost:"+TEST_PORT).build(), mapperFacade);
    }
}