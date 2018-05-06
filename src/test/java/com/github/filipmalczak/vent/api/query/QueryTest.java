package com.github.filipmalczak.vent.api.query;

import com.github.filipmalczak.vent.VentSpringTest;
import com.github.filipmalczak.vent.api.ObjectSnapshot;
import com.github.filipmalczak.vent.api.VentId;
import com.github.filipmalczak.vent.api.query.operator.EqualsOperator;
import com.github.filipmalczak.vent.api.query.operator.Operator;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.embedded.service.SnapshotService;
import com.github.filipmalczak.vent.testimpl.TestingTemporalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.filipmalczak.vent.helper.Struct.list;
import static com.github.filipmalczak.vent.helper.Struct.map;
import static com.github.filipmalczak.vent.helper.Struct.pair;
import static org.junit.jupiter.api.Assertions.assertEquals;

//fixme these tests need a total refactor, but for now I'm just checking whether the approach will work at all
@VentSpringTest
public class QueryTest {
    //fixme in this case standard temporal service could be enough
    @Autowired
    private TestingTemporalService temporalService;
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;
    @Autowired
    private SnapshotService snapshotService;

    @Autowired
    private ReactiveVentDb reactiveVentDb;

    private final static String COLLECTION_NAME = "collection";
    private final static LocalDateTime START_TIME = LocalDateTime.of(2000, 1, 1, 12, 0);
    private final static Duration INTERVAL = Duration.ofMinutes(5);

    private Query query(Operator rootOperator){
        return new Query(COLLECTION_NAME, rootOperator, temporalService, mongoTemplate, snapshotService);
    }

    private Map<String, VentId> fixtureNameToId;

    @Test
    public void queryingEmptyDbShouldYieldNoResults(){
        temporalService.addResults(LocalDateTime.now());
        StepVerifier.create(
            query(
                //todo switch to spock, unroll with many operators
                EqualsOperator.with("a", 1)
            ).execute(temporalService.now())
        ).verifyComplete();
    }

    private List<LocalDateTime> byInterval(int size){
        return IntStream.range(0, size).
            mapToObj(this::after).
            collect(Collectors.toList());
    }

    private LocalDateTime after(int intervals){
        return START_TIME.plus(INTERVAL.multipliedBy(intervals));
    }

    private Map account(String name, int balance){
        return map(
            pair("accountName", name),
            pair("balance", balance)
        );
    }

    private Map person(String firstName, String lastName, int age, String street, String city, List<Map> accounts){
        return map(
            pair("name", map(
                pair("first", firstName),
                pair("last", lastName)
            )),
            pair("age", age),
            pair("address", map(
                pair("street", street),
                pair("city", city)
            )),
            pair("accounts", accounts)
        );
    }

    @Test
    public void queryingInitialStateByTopLevelFieldShouldWorkProperly(){
        temporalService.addResults(byInterval(3));
        Map aData = person(
            "A1", "A2",
            20,
            "S1", "C1",
            list(
                account("AA1", 100),
                account("AA2", 200),
                account("AA3", 300)
            )
        );
        VentId a = reactiveVentDb.asBlocking().getCollection(COLLECTION_NAME).create(aData);
        Map bData = person(
            "B1", "B2",
            25,
            "S2", "C1",
            list(
                account("AB1", 200),
                account("AB2", 500),
                account("AB3", 30)
            )
        );
        VentId b = reactiveVentDb.asBlocking().getCollection(COLLECTION_NAME).create(bData);

        Query query = query(EqualsOperator.with("age", 20));
        List<ObjectSnapshot> results = query.execute(temporalService.now()).toStream().collect(Collectors.toList());
        assertEquals(
            list(
                ObjectSnapshot.builder().
                    ventId(a).
                    state(aData).
                    lastUpdate(after(0)).
                    queryTime(after(2)).
                    version(0).build()
            ),
            results
        );
    }

    @Test
    public void queryingEventResultsByTopLevelFieldShouldWorkProperly(){
        temporalService.addResults(byInterval(5));
        Map aData = person(
            "A1", "A2",
            20,
            "S1", "C1",
            list(
                account("AA1", 100),
                account("AA2", 200),
                account("AA3", 300)
            )
        );
        VentId a = reactiveVentDb.asBlocking().getCollection(COLLECTION_NAME).create(aData);
        Map bData = person(
            "B1", "B2",
            25,
            "S2", "C1",
            list(
                account("AB1", 200),
                account("AB2", 500),
                account("AB3", 30)
            )
        );
        VentId b = reactiveVentDb.asBlocking().getCollection(COLLECTION_NAME).create(bData);

        reactiveVentDb.asBlocking().getCollection(COLLECTION_NAME).putValue(a, "age", 30);
        reactiveVentDb.asBlocking().getCollection(COLLECTION_NAME).putValue(b, "age", 35);

        Query query = query(EqualsOperator.with("age", 35));
        List<ObjectSnapshot> results = query.execute(temporalService.now()).toStream().collect(Collectors.toList());
        assertEquals(
            list(
                ObjectSnapshot.builder().
                    ventId(b).
                    state(map(bData, pair("age", 35))).
                    lastUpdate(after(3)).
                    queryTime(after(4)).
                    version(1).build()
            ),
            results
        );
    }
}