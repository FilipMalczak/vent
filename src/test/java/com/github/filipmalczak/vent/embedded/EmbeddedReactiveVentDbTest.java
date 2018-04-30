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
    public void defaultCreateThenGetInTheFuture(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plus(Duration.ofSeconds(3));
        temporalService.addResult(now);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create().
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, future))
        ).expectNext(
            ObjectSnapshot.builder().
                state(new HashMap<>()).
                version(0).
                lastUpdate(now).
                queryTime(future).
                build()
        ).verifyComplete();
    }


    @Test
    public void createExplicitlyEmptyThenGetInTheFuture(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plus(Duration.ofSeconds(3));
        temporalService.addResult(now);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create(new HashMap()).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, future))
        ).expectNext(
            ObjectSnapshot.builder().
                state(new HashMap<>()).
                version(0).
                lastUpdate(now).
                queryTime(future).
                build()
        ).verifyComplete();
    }

    @Test
    public void createNonEmptyThenGetInTheFuture(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plus(Duration.ofSeconds(3));
        temporalService.addResult(now);

        Map data = new HashMap<>();
        data.put("a", 1);
        data.put("b", asList("x", "y"));

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create(data).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, future))
        ).expectNext(
            ObjectSnapshot.builder().
                state(data).
                version(0).
                lastUpdate(now).
                queryTime(future).
                build()
        ).verifyComplete();
    }


    @Test
    public void defaultCreateThenGetInThePast(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = now.minus(Duration.ofSeconds(3));
        temporalService.addResult(now);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create().
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, past))
        ).verifyComplete();
    }


    @Test
    public void createExplicitlyEmptyThenGetInThePast(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = now.minus(Duration.ofSeconds(3));
        temporalService.addResult(now);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create(new HashMap()).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, past))
        ).verifyComplete();
    }

    @Test
    public void createNonEmptyThenGetInThePast(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = now.minus(Duration.ofSeconds(3));
        temporalService.addResult(now);

        Map data = new HashMap<>();
        data.put("a", 1);
        data.put("b", asList("x", "y"));

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create(data).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, past))
        ).verifyComplete();
    }
}