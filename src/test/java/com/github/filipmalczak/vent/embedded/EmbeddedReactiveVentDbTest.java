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

import static com.github.filipmalczak.vent.helper.Struct.list;
import static com.github.filipmalczak.vent.helper.Struct.map;
import static com.github.filipmalczak.vent.helper.Struct.pair;
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
                state(map()).
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
            ventDb.getCollection(TEST_COLLECTION).create(map()).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, now))
        ).expectNext(
            ObjectSnapshot.builder().
                state(map()).
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

        Map data = map();
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
                state(map()).
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
            ventDb.getCollection(TEST_COLLECTION).create(map()).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, future))
        ).expectNext(
            ObjectSnapshot.builder().
                state(map()).
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

        Map data = map();
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
            ventDb.getCollection(TEST_COLLECTION).create(map()).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, past))
        ).verifyComplete();
    }

    @Test
    public void createNonEmptyThenGetInThePast(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = now.minus(Duration.ofSeconds(3));
        temporalService.addResult(now);

        Map data = map(pair("a", 1), pair("x", "y"), pair("b", pair("c", "d")));

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create(data).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, past))
        ).verifyComplete();
    }


    @Test
    public void putTopLevelAndGetInTheFuture(){
        LocalDateTime createNow = LocalDateTime.now();
        LocalDateTime putNow = LocalDateTime.now();
        LocalDateTime future = putNow.plus(Duration.ofSeconds(3));
        temporalService.addResult(createNow, putNow);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create().
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).putValue(ventId, "a", 1)).
                flatMap(eventConfirmation -> ventDb.getCollection(TEST_COLLECTION).get(eventConfirmation.getVentId(), future))
        ).expectNext(
            ObjectSnapshot.builder().
                state(pair("a", 1)).
                version(1).
                lastUpdate(putNow).
                queryTime(future).
                build()
        ).verifyComplete();
    }

    @Test
    public void putTopLevelAndGetBeforePut(){
        LocalDateTime createNow = LocalDateTime.now();
        LocalDateTime putNow = createNow.plus(Duration.ofSeconds(2));
        LocalDateTime beforePut = putNow.minus(Duration.ofSeconds(1));
        temporalService.addResult(createNow, putNow);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create().
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).putValue(ventId, "a", 1)).
                flatMap(eventConfirmation -> ventDb.getCollection(TEST_COLLECTION).get(eventConfirmation.getVentId(), beforePut))
        ).expectNext(
            ObjectSnapshot.builder().
                state(map()).
                version(0).
                lastUpdate(createNow).
                queryTime(beforePut).
                build()
        ).verifyComplete();
    }

    @Test
    public void putTopLevelListAndChangeByIndex(){
        LocalDateTime createNow = LocalDateTime.now();
        LocalDateTime putNow = createNow.plus(Duration.ofSeconds(1));
        LocalDateTime put2Now = putNow.plus(Duration.ofSeconds(1));
        LocalDateTime queryTime = put2Now.plus(Duration.ofSeconds(1));
        temporalService.addResult(createNow, putNow, put2Now);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create().
                flatMap(ventId->
                    ventDb.getCollection(TEST_COLLECTION).
                        putValue(ventId, "a", list(1, 2, 3))
                ).
                flatMap(eventConfirmation ->
                    ventDb.getCollection(TEST_COLLECTION).
                        putValue(eventConfirmation.getVentId(), "a[1]", 5)
                ).
                flatMap(eventConfirmation ->
                    ventDb.getCollection(TEST_COLLECTION).get(eventConfirmation.getVentId(), queryTime)
                )
        ).expectNext(
            ObjectSnapshot.builder().
                state(map(pair("a", list(1, 5, 3)))).
                version(2).
                lastUpdate(put2Now).
                queryTime(queryTime).
                build()
        ).verifyComplete();
    }
}