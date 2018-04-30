package com.github.filipmalczak.vent.embedded;

import com.github.filipmalczak.vent.VentSpringTest;
import com.github.filipmalczak.vent.embedded.model.ObjectSnapshot;
import com.github.filipmalczak.vent.embedded.service.TestingTemporalService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;

@VentSpringTest
@Slf4j
class EmbeddedReactiveVentDbTest {
    @Autowired
    private EmbeddedReactiveVentDb ventDb;

    @Autowired
    private TestingTemporalService temporalService;

    private static final String TEST_COLLECTION = "test_collection";

    @Test
    public void defaultCreateThenGetAtCreationTime(){
        LocalDateTime now = LocalDateTime.now();
        temporalService.addResult(now);

        StepVerifier.create(
        ventDb.getCollection(TEST_COLLECTION).create().
            flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, now))
        ).expectNext(
            ObjectSnapshot.builder().
                state(new HashMap<>()).
                version(0).
                lastUpdate(now).
                queryTime(now).
                build()
        ).verifyComplete();
    }

    @Test
    public void createExplicitlyEmptyThenGetAtCreationTime(){
        LocalDateTime now = LocalDateTime.now();
        temporalService.addResult(now);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create(new HashMap()).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, now))
        ).expectNext(
            ObjectSnapshot.builder().
                state(new HashMap<>()).
                version(0).
                lastUpdate(now).
                queryTime(now).
                build()
        ).verifyComplete();
    }


    @Test
    public void createNonEmptyThenGetAtCreationTime(){
        LocalDateTime now = LocalDateTime.now();
        temporalService.addResult(now);

        Map data = new HashMap<>();
        data.put("a", 1);
        data.put("b", asList("x", "y"));

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create(data).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, now))
        ).expectNext(
            ObjectSnapshot.builder().
                state(data).
                version(0).
                lastUpdate(now).
                queryTime(now).
                build()
        ).verifyComplete();
    }

    @Test
    @SneakyThrows
    public void defaultCreateThenGetMomentLater(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime now2 = now.plus(Duration.ofSeconds(3));
        temporalService.addResult(now);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create().
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, now2)).
                log("XXX")
        ).expectNext(
            ObjectSnapshot.builder().
                state(new HashMap<>()).
                version(0).
                lastUpdate(now).
                queryTime(now2).
                build()
        );
    }


    @Test
    public void createExplicitlyEmptyThenGetMomentLater(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime now2 = now.plus(Duration.ofSeconds(3));
        temporalService.addResult(now);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create(new HashMap()).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, now2))
        ).expectNext(
            ObjectSnapshot.builder().
                state(new HashMap<>()).
                version(0).
                lastUpdate(now).
                queryTime(now2).
                build()
        );
    }

    @Test
    public void createNonEmptyThenGetMomentLater(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime now2 = now.plus(Duration.ofSeconds(3));
        temporalService.addResult(now);

        Map data = new HashMap<>();
        data.put("a", 1);
        data.put("b", asList("x", "y"));

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create(data).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, now2))
        ).expectNext(
            ObjectSnapshot.builder().
                state(data).
                version(0).
                lastUpdate(now).
                queryTime(now2).
                build()
        );
    }
}