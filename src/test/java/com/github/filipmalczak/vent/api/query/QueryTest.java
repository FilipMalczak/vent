package com.github.filipmalczak.vent.api.query;

import com.github.filipmalczak.vent.VentSpringTest;
import com.github.filipmalczak.vent.api.ObjectSnapshot;
import com.github.filipmalczak.vent.api.VentId;
import com.github.filipmalczak.vent.api.query.operator.*;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.embedded.service.MongoQueryPreparator;
import com.github.filipmalczak.vent.embedded.service.SnapshotService;
import com.github.filipmalczak.vent.testimpl.TestingTemporalService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static com.github.filipmalczak.vent.helper.Struct.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

//fixme these tests need a total refactor, but for now I'm just checking whether the approach will work at all
//todo use nested tests
@VentSpringTest
@Slf4j
public class QueryTest {
    //fixme in this case standard temporal service could be enough
    @Autowired
    private TestingTemporalService temporalService;
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;
    @Autowired
    private SnapshotService snapshotService;
    @Autowired
    private MongoQueryPreparator mongoQueryPreparator;

    @Autowired
    private ReactiveVentDb reactiveVentDb;

    private final static String COLLECTION_NAME = "collection";
    private final static LocalDateTime START_TIME = LocalDateTime.of(2000, 1, 1, 12, 0);
    private final static Duration INTERVAL = Duration.ofMinutes(5);

    private Query query(Operator rootOperator){
        return new Query(COLLECTION_NAME, rootOperator, mongoQueryPreparator, mongoTemplate, snapshotService);
    }

    private Map<String, VentId> fixtureNameToId;

    @BeforeEach
    public void setUp(){
        reactiveVentDb.asBlocking().getCollection(COLLECTION_NAME).drop();
    }

    @Test
    //todo switch to spock, unroll with many operators
    public void queryingEmptyDbShould(){
        temporalService.withResults(list(LocalDateTime.now()), () -> {
            StepVerifier.create(
                query(EqualsOperator.with("a", 1)).
                    execute(temporalService.now())
            ).verifyComplete();
        });

        temporalService.withResults(list(LocalDateTime.now()), () -> {
            assertNull(
                query(EqualsOperator.with("a", 1)).
                    execute(temporalService.now()).
                    blockFirst()
            );
        });
    }

    @Test
    public void queryInitialStateByTopLevelField(){
        temporalService.withResults(byInterval(3), () -> {
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
            List<ObjectSnapshot> results = query.execute(temporalService.now()).toStream().collect(toList());
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
        });

    }

    @Test
    public void queryingEventResultsByTopLevelField() {
        temporalService.withResults(byInterval(5), () -> {
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
            List<ObjectSnapshot> results = query.execute(temporalService.now()).toStream().collect(toList());
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
        });
    }

    @Test
    public void queryingInitialStateByNestedField() {
        temporalService.withResults(byInterval(3), () -> {
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

            Query query = query(EqualsOperator.with("name.last", "A2"));
            List<ObjectSnapshot> results = query.execute(temporalService.now()).toStream().collect(toList());
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
        });
    }

    @Test
    public void queryingExplicitEventResultsNestedField() {
        temporalService.withResults(byInterval(5), () -> {
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

            reactiveVentDb.asBlocking().getCollection(COLLECTION_NAME).putValue(a, "name.first", "X");
            reactiveVentDb.asBlocking().getCollection(COLLECTION_NAME).putValue(b, "name.first", "Y");

            Query query = query(EqualsOperator.with("name.first", "Y"));
            List<ObjectSnapshot> results = query.execute(temporalService.now()).toStream().collect(toList());
            assertEquals(
                list(
                    ObjectSnapshot.builder().
                        ventId(b).
                        state(map(
                            bData,
                            pair("name", map(
                                pair("first", "Y"),
                                pair("last", ((Map<String, ?>)bData.get("name")).get("last"))
                            ))
                        )).
                        lastUpdate(after(3)).
                        queryTime(after(4)).
                        version(1).build()
                ),
                results
            );
        });
    }

    @Test
    public void queryingSuperpathEventResultsNestedField() {
        temporalService.withResults(byInterval(5), () -> {
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

            reactiveVentDb.asBlocking().getCollection(COLLECTION_NAME).
                putValue(a, "name", map(
                    pair("first", "X1"),
                    pair("last", "X2")
                ));
            reactiveVentDb.asBlocking().getCollection(COLLECTION_NAME).
                putValue(b, "name", map(
                    pair("first", "Y1"),
                    pair("last", "Y2")
                ));

            Query query = query(EqualsOperator.with("name.last", "X2"));
            List<ObjectSnapshot> results = query.execute(temporalService.now()).toStream().collect(toList());
            assertEquals(
                list(
                    ObjectSnapshot.builder().
                        ventId(a).
                        state(map(
                            aData,
                            pair("name", map(
                                pair("first", "X1"),
                                pair("last", "X2")
                            ))
                        )).
                        lastUpdate(after(2)).
                        queryTime(after(4)).
                        version(1).build()
                ),
                results
            );
        });
    }

    @Test
    public void queryingAnd() {
        temporalService.withResults(byInterval(5), () -> {
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
            Map cData = person(
                "B3", "B2",
                35,
                "S2", "C1",
                list(
                    account("AB1", 200),
                    account("AB2", 500),
                    account("AB3", 30)
                )
            );
            VentId c = reactiveVentDb.asBlocking().getCollection(COLLECTION_NAME).create(cData);
            Map dData = person(
                "B3", "B2",
                40,
                "S3", "C1",
                list(
                    account("AB1", 200),
                    account("AB2", 500),
                    account("AB3", 30)
                )
            );
            VentId d = reactiveVentDb.asBlocking().getCollection(COLLECTION_NAME).create(dData);

            Query query = query(
                AndOperator.builder().
                    operand(EqualsOperator.with("address.street", "S2")).
                    operand(EqualsOperator.with("name.last", "B2")).
                    build()

            );
            log.info("Query: "+query);
            Set<ObjectSnapshot> results = query.execute(temporalService.now()).toStream().collect(toSet());
            assertEquals(
                set(
                    ObjectSnapshot.builder().
                        ventId(b).
                        state(bData).
                        lastUpdate(after(1)).
                        queryTime(after(4)).
                        version(0).build(),
                    ObjectSnapshot.builder().
                        ventId(c).
                        state(cData).
                        lastUpdate(after(2)).
                        queryTime(after(4)).
                        version(0).build()
                ),
                results
            );
        });
    }

    @Test
    public void queryingOr() {
        temporalService.withResults(byInterval(5), () -> {
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
            Map cData = person(
                "C1", "C2",
                35,
                "S2", "C1",
                list(
                    account("AB1", 200),
                    account("AB2", 500),
                    account("AB3", 30)
                )
            );
            VentId c = reactiveVentDb.asBlocking().getCollection(COLLECTION_NAME).create(cData);
            Map dData = person(
                "D1", "D2",
                40,
                "S3", "C1",
                list(
                    account("AB1", 200),
                    account("AB2", 500),
                    account("AB3", 30)
                )
            );
            VentId d = reactiveVentDb.asBlocking().getCollection(COLLECTION_NAME).create(dData);

            Query query = query(
                OrOperator.builder().
                    operand(EqualsOperator.with("name.first", "B1")).
                    operand(EqualsOperator.with("name.last", "C2")).
                    build()

            );
            Set<ObjectSnapshot> results = query.execute(temporalService.now()).toStream().collect(toSet());
            assertEquals(
                set(
                    ObjectSnapshot.builder().
                        ventId(b).
                        state(bData).
                        lastUpdate(after(1)).
                        queryTime(after(4)).
                        version(0).build(),
                    ObjectSnapshot.builder().
                        ventId(c).
                        state(cData).
                        lastUpdate(after(2)).
                        queryTime(after(4)).
                        version(0).build()
                ),
                results
            );
        });
    }

    @Test
    public void queryingBySublistIndexFromInitialState() {
        temporalService.withResults(byInterval(3), () -> {
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

            Query query = query(EqualsOperator.with("accounts[0].balance", 100));
            List<ObjectSnapshot> results = query.execute(temporalService.now()).toStream().collect(toList());
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
        });
    }

    @Test
    public void queryingBySublistIndexFromExplicitEvent() {
        temporalService.withResults(byInterval(4), () -> {
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

            reactiveVentDb.asBlocking().getCollection(COLLECTION_NAME).putValue(a, "accounts[0].balance", 1234);

            Query query = query(EqualsOperator.with("accounts[0].balance", 1234));
            List<ObjectSnapshot> results = query.execute(temporalService.now()).toStream().collect(toList());
            assertEquals(
                list(
                    ObjectSnapshot.builder().
                        ventId(a).
                        state(map(
                            aData,
                            pair("accounts", list(
                                account("AA1", 1234),
                                account("AA2", 200),
                                account("AA3", 300)
                            ))
                        )).
                        lastUpdate(after(2)).
                        queryTime(after(3)).
                        version(1).build()
                ),
                results
            );
        });
    }

    @Test
    public void queryingBySublistIndexFromLowerSuperPathWhenInitialStateIndicatesCandidacy() {
        temporalService.withResults(byInterval(4), () -> {
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

            reactiveVentDb.asBlocking().getCollection(COLLECTION_NAME).putValue(a, "accounts[0]", map(pair("accountName", "AAA"), pair("balance", 314)));

            Query query = query(EqualsOperator.with("accounts[0].balance", 314));
            List<ObjectSnapshot> results = query.execute(temporalService.now()).toStream().collect(toList());
            assertEquals(
                list(
                    ObjectSnapshot.builder().
                        ventId(a).
                        state(map(
                            aData,
                            pair("accounts", list(
                                account("AAA", 314),
                                account("AA2", 200),
                                account("AA3", 300)
                            ))
                        )).
                        lastUpdate(after(2)).
                        queryTime(after(3)).
                        version(1).build()
                ),
                results
            );
        });
    }

    //fixme how does it work? PathUtils have a bug, so what the hell? oO
    @Test
    public void queryingBySublistIndexFromHigherSuperPathWhenInitialStateIndicatesCandidacy() {
        temporalService.withResults(byInterval(4), () -> {
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

            reactiveVentDb.asBlocking().getCollection(COLLECTION_NAME).putValue(a, "accounts", list(map(pair("accountName", "AAA"), pair("balance", 314))));

            Query query = query(EqualsOperator.with("accounts[0].balance", 314));
            List<ObjectSnapshot> results = query.execute(temporalService.now()).toStream().collect(toList());
            assertEquals(
                list(
                    ObjectSnapshot.builder().
                        ventId(a).
                        state(map(
                            aData,
                            pair("accounts", list(
                                account("AAA", 314)
                            ))
                        )).
                        lastUpdate(after(2)).
                        queryTime(after(3)).
                        version(1).build()
                ),
                results
            );
        });
    }

    @Test
    public void queryingNot() {
        temporalService.withResults(byInterval(3), () -> {
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

            Query query = query(NotOperator.of(EqualsOperator.with("accounts[0].balance", 200)));
            List<ObjectSnapshot> results = query.execute(temporalService.now()).toStream().collect(toList());
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
        });
    }

    private List<LocalDateTime> byInterval(int size){
        return IntStream.range(0, size).
            mapToObj(this::after).
            collect(toList());
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
}