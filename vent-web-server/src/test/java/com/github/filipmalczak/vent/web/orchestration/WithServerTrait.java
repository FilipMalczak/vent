package com.github.filipmalczak.vent.web.orchestration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public interface WithServerTrait {
    @BeforeAll
    static void setServerUp(){
        TestServerManager.start();
    }

    @AfterAll
    static void tearServerDown(){
        TestServerManager.stop();
    }
}
