package com.github.filipmalczak.vent.embedded;

import com.github.filipmalczak.vent.VentSpringTest;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.testing.TestingTemporalService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static com.github.filipmalczak.vent.helper.Struct.*;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@VentSpringTest
@Slf4j
class EmbeddedReactiveVentDbTest {
    @Autowired
    private EmbeddedReactiveVentDb ventDb;

    @Autowired
    private TestingTemporalService temporalService;

    private static final String TEST_COLLECTION = "test_collection";

    private Holder<VentId> holder;

    @BeforeEach
    public void setUp(){
        holder = new Holder<>();
        ventDb.initialize();
    }

    /**
     * Sorta ugly, but it gets the work done, so why not?
     * Just a reference wrapper that provides one method T -> T that stores argument inside the wrapper and returns it.
     * Handy for using values provided as results in assertions (like ID).
     */
    private static class Holder<T> {
        @Getter private T value;

        public T hold(T val){
            value = val;
            return val;
        }
    }

    @Test
    public void defaultCreateThenGetAtCreationTime(){
        LocalDateTime now = LocalDateTime.now();
        temporalService.addResults(now);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create().
                map(holder::hold).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, now))
        ).assertNext( o ->
            assertEquals(
                ObjectSnapshot.builder().
                    ventId(holder.getValue()).
                    state(map()).
                    version(0).
                    lastUpdate(now).
                    queryTime(now).
                    build(),
                o
            )
        ).verifyComplete();
    }

    @Test
    public void createExplicitlyEmptyThenGetAtCreationTime(){
        LocalDateTime now = LocalDateTime.now();
        temporalService.addResults(now);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create(map()).
                map(holder::hold).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, now))
        ).assertNext( o ->
            assertEquals(
                ObjectSnapshot.builder().
                    ventId(holder.getValue()).
                    state(map()).
                    version(0).
                    lastUpdate(now).
                    queryTime(now).
                    build(),
                o
            )
        ).verifyComplete();
    }


    @Test
    public void createNonEmptyThenGetAtCreationTime(){
        LocalDateTime now = LocalDateTime.now();
        temporalService.addResults(now);

        Map data = map();
        data.put("a", 1);
        data.put("b", asList("x", "y"));

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create(data).
                map(holder::hold).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, now))
        ).assertNext( o ->
            assertEquals(
                ObjectSnapshot.builder().
                    ventId(holder.getValue()).
                    state(data).
                    version(0).
                    lastUpdate(now).
                    queryTime(now).
                    build(),
                o
            )
        ).verifyComplete();
    }

    @Test
    public void defaultCreateThenGetInTheFuture(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plus(Duration.ofSeconds(3));
        temporalService.addResults(now);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create().
                map(holder::hold).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, future))
        ).assertNext( o ->
            assertEquals(
                ObjectSnapshot.builder().
                    ventId(holder.getValue()).
                    state(map()).
                    version(0).
                    lastUpdate(now).
                    queryTime(future).
                    build(),
                o
            )
        ).verifyComplete();
    }


    @Test
    public void createExplicitlyEmptyThenGetInTheFuture(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plus(Duration.ofSeconds(3));
        temporalService.addResults(now);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create(map()).
                map(holder::hold).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, future))
        ).assertNext( o ->
            assertEquals(
                ObjectSnapshot.builder().
                    ventId(holder.getValue()).
                    state(map()).
                    version(0).
                    lastUpdate(now).
                    queryTime(future).
                    build(),
                o
            )
        ).verifyComplete();
    }

    @Test
    public void createNonEmptyThenGetInTheFuture(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plus(Duration.ofSeconds(3));
        temporalService.addResults(now);

        Map data = map();
        data.put("a", 1);
        data.put("b", asList("x", "y"));

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create(data).
                map(holder::hold).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, future))
        ).assertNext( o ->
            assertEquals(
                ObjectSnapshot.builder().
                    ventId(holder.getValue()).
                    state(data).
                    version(0).
                    lastUpdate(now).
                    queryTime(future).
                    build(),
                o
            )
        ).verifyComplete();
    }


    @Test
    public void defaultCreateThenGetInThePast(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = now.minus(Duration.ofSeconds(3));
        temporalService.addResults(now);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create().
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, past))
        ).verifyComplete();
    }


    @Test
    public void createExplicitlyEmptyThenGetInThePast(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = now.minus(Duration.ofSeconds(3));
        temporalService.addResults(now);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create(map()).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).get(ventId, past))
        ).verifyComplete();
    }

    @Test
    public void createNonEmptyThenGetInThePast(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = now.minus(Duration.ofSeconds(3));
        temporalService.addResults(now);

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
        temporalService.addResults(createNow, putNow);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create().
                map(holder::hold).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).putValue(ventId, "a", 1)).
                flatMap(eventConfirmation -> ventDb.getCollection(TEST_COLLECTION).get(eventConfirmation.getVentId(), future))
        ).assertNext( o ->
            assertEquals(
                ObjectSnapshot.builder().
                    ventId(holder.getValue()).
                    state(pair("a", 1)).
                    version(1).
                    lastUpdate(putNow).
                    queryTime(future).
                    build(),
                o
            )
        ).verifyComplete();
    }

    @Test
    public void putTopLevelAndGetBeforePut(){
        LocalDateTime createNow = LocalDateTime.now();
        LocalDateTime putNow = createNow.plus(Duration.ofSeconds(2));
        LocalDateTime beforePut = putNow.minus(Duration.ofSeconds(1));
        temporalService.addResults(createNow, putNow);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create().
                map(holder::hold).
                flatMap(ventId -> ventDb.getCollection(TEST_COLLECTION).putValue(ventId, "a", 1)).
                flatMap(eventConfirmation -> ventDb.getCollection(TEST_COLLECTION).get(eventConfirmation.getVentId(), beforePut))
        ).assertNext( o ->
            assertEquals(
                ObjectSnapshot.builder().
                    ventId(holder.getValue()).
                    state(map()).
                    version(0).
                    lastUpdate(createNow).
                    queryTime(beforePut).
                    build(),
                o
            )
        ).verifyComplete();
    }

    @Test
    public void putTopLevelListAndChangeByIndex(){
        LocalDateTime createNow = LocalDateTime.now();
        LocalDateTime putNow = createNow.plus(Duration.ofSeconds(1));
        LocalDateTime put2Now = putNow.plus(Duration.ofSeconds(1));
        LocalDateTime queryTime = put2Now.plus(Duration.ofSeconds(1));
        temporalService.addResults(createNow, putNow, put2Now);

        StepVerifier.create(
            ventDb.getCollection(TEST_COLLECTION).create().
                map(holder::hold).
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
        ).assertNext( o ->
            assertEquals(
                ObjectSnapshot.builder().
                    ventId(holder.getValue()).
                    state(map(pair("a", list(1, 5, 3)))).
                    version(2).
                    lastUpdate(put2Now).
                    queryTime(queryTime).
                    build(),
                o
            )
        ).verifyComplete();
    }
}