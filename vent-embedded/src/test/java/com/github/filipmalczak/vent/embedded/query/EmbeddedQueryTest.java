package com.github.filipmalczak.vent.embedded.query;

import com.github.filipmalczak.vent.VentSpringTest;
import com.github.filipmalczak.vent.api.blocking.BlockingVentDb;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.embedded.query.operator.*;
import com.github.filipmalczak.vent.embedded.service.MongoQueryPreparator;
import com.github.filipmalczak.vent.embedded.service.SnapshotService;
import com.github.filipmalczak.vent.testing.TestingTemporalService;
import com.github.filipmalczak.vent.testing.Times;
import com.github.filipmalczak.vent.traits.paradigm.Blocking;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.filipmalczak.vent.adapter.Adapters.adapt;
import static com.github.filipmalczak.vent.helper.Struct.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

//fixme these tests need a total refactor, but for now I'm just checking whether the approach will work at all
@VentSpringTest
@Slf4j
public class EmbeddedQueryTest {
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

    private BlockingVentDb blockingVentDb;

    private final static String COLLECTION_NAME = "collection";
    private final static Times times = Times.defaultFromMilleniumBreak();

    // we could do this in tearDown/@AfterEach, but its better to clean the DB up before test,
    // so that we can investigate DB state post factum
    @BeforeEach
    public void setUp(){
        blockingVentDb = (BlockingVentDb) adapt(reactiveVentDb, Blocking.class);
        blockingVentDb.getCollection(COLLECTION_NAME).drop();
    }

    @Nested
    @DisplayName("Test behaviour of completely empty DB")
    public class EmptyDb {
        @Test
        @DisplayName("Query empty DB in reactive way")
        public void reactiveQuery(){
            temporalService.withResults(times.justNow(), () -> {
                StepVerifier.create(
                    query(EqualsOperator.with("a", 1)).
                        find(temporalService.now())
                ).verifyComplete();
            });
        }

        @Test
        @DisplayName("Query empty DB in adapters way")
        public void blockingQuery(){
            temporalService.withResults(times.justNow(), () -> {
                assertNull(
                    query(EqualsOperator.with("a", 1)).
                        find(temporalService.now()).
                        blockFirst()
                );
            });
        }
    }

    @Nested
    @DisplayName("Test equals operator")
    public class Equals {
        @Nested
        @DisplayName("Test querying of objects in their initial state")
        public class InitialState {
            @Test
            @DisplayName("Query by top level field")
            public void byTopLevelField(){
                temporalService.withResults(times.byInterval(3), () -> {
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
                    VentId a = blockingVentDb.getCollection(COLLECTION_NAME).create(aData);
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
                    VentId b = blockingVentDb.getCollection(COLLECTION_NAME).create(bData);

                    EmbeddedReactiveQuery query = query(EqualsOperator.with("age", 20));
                    List<ObjectSnapshot> results = query.find(temporalService.now()).toStream().collect(toList());
                    assertEquals(
                        list(
                            ObjectSnapshot.builder().
                                ventId(a).
                                state(aData).
                                lastUpdate(times.after(0)).
                                queryTime(times.after(2)).
                                version(0).build()
                        ),
                        results
                    );
                });

            }

            @Test
            @DisplayName("Query by nested field")
            public void byNestedField() {
                temporalService.withResults(times.byInterval(3), () -> {
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
                    VentId a = blockingVentDb.getCollection(COLLECTION_NAME).create(aData);
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
                    VentId b = blockingVentDb.getCollection(COLLECTION_NAME).create(bData);

                    EmbeddedReactiveQuery query = query(EqualsOperator.with("name.last", "A2"));
                    List<ObjectSnapshot> results = query.find(temporalService.now()).toStream().collect(toList());
                    assertEquals(
                        list(
                            ObjectSnapshot.builder().
                                ventId(a).
                                state(aData).
                                lastUpdate(times.after(0)).
                                queryTime(times.after(2)).
                                version(0).build()
                        ),
                        results
                    );
                });
            }


            @Test
            @DisplayName("Query by nested list index")
            public void byNestedListIndex() {
                temporalService.withResults(times.byInterval(3), () -> {
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
                    VentId a = blockingVentDb.getCollection(COLLECTION_NAME).create(aData);
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
                    VentId b = blockingVentDb.getCollection(COLLECTION_NAME).create(bData);

                    EmbeddedReactiveQuery query = query(EqualsOperator.with("accounts[0].balance", 100));
                    List<ObjectSnapshot> results = query.find(temporalService.now()).toStream().collect(toList());
                    assertEquals(
                        list(
                            ObjectSnapshot.builder().
                                ventId(a).
                                state(aData).
                                lastUpdate(times.after(0)).
                                queryTime(times.after(2)).
                                version(0).build()
                        ),
                        results
                    );
                });
            }

        }

        @Nested
        @DisplayName("Test querying of objects in state after some events")
        public class EventResults {

            @Test
            @DisplayName("Query by top level field")
            public void byTopLevelField() {
                temporalService.withResults(times.byInterval(5), () -> {
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
                    VentId a = blockingVentDb.getCollection(COLLECTION_NAME).create(aData);
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
                    VentId b = blockingVentDb.getCollection(COLLECTION_NAME).create(bData);

                    blockingVentDb.getCollection(COLLECTION_NAME).putValue(a, "age", 30);
                    blockingVentDb.getCollection(COLLECTION_NAME).putValue(b, "age", 35);

                    EmbeddedReactiveQuery query = query(EqualsOperator.with("age", 35));
                    List<ObjectSnapshot> results = query.find(temporalService.now()).toStream().collect(toList());
                    assertEquals(
                        list(
                            ObjectSnapshot.builder().
                                ventId(b).
                                state(map(bData, pair("age", 35))).
                                lastUpdate(times.after(3)).
                                queryTime(times.after(4)).
                                version(1).build()
                        ),
                        results
                    );
                });
            }


            @Test
            @DisplayName("Query by nested field set with explicit PutValue")
            public void byNestedFieldWithExplicitPut() {
                temporalService.withResults(times.byInterval(5), () -> {
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
                    VentId a = blockingVentDb.getCollection(COLLECTION_NAME).create(aData);
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
                    VentId b = blockingVentDb.getCollection(COLLECTION_NAME).create(bData);

                    blockingVentDb.getCollection(COLLECTION_NAME).putValue(a, "name.first", "X");
                    blockingVentDb.getCollection(COLLECTION_NAME).putValue(b, "name.first", "Y");

                    EmbeddedReactiveQuery query = query(EqualsOperator.with("name.first", "Y"));
                    List<ObjectSnapshot> results = query.find(temporalService.now()).toStream().collect(toList());
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
                                lastUpdate(times.after(3)).
                                queryTime(times.after(4)).
                                version(1).build()
                        ),
                        results
                    );
                });
            }

            @Test
            @DisplayName("Query by nested field set with PutValue for superpath")
            public void byNestedFieldWithSuperpathPut() {
                temporalService.withResults(times.byInterval(5), () -> {
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
                    VentId a = blockingVentDb.getCollection(COLLECTION_NAME).create(aData);
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
                    VentId b = blockingVentDb.getCollection(COLLECTION_NAME).create(bData);

                    blockingVentDb.getCollection(COLLECTION_NAME).
                        putValue(a, "name", map(
                            pair("first", "X1"),
                            pair("last", "X2")
                        ));
                    blockingVentDb.getCollection(COLLECTION_NAME).
                        putValue(b, "name", map(
                            pair("first", "Y1"),
                            pair("last", "Y2")
                        ));

                    EmbeddedReactiveQuery query = query(EqualsOperator.with("name.last", "X2"));
                    List<ObjectSnapshot> results = query.find(temporalService.now()).toStream().collect(toList());
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
                                lastUpdate(times.after(2)).
                                queryTime(times.after(4)).
                                version(1).build()
                        ),
                        results
                    );
                });
            }

            @Test
            @DisplayName("Query by nested list index")
            public void byNestedListIndex() {
                temporalService.withResults(times.byInterval(4), () -> {
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
                    VentId a = blockingVentDb.getCollection(COLLECTION_NAME).create(aData);
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
                    VentId b = blockingVentDb.getCollection(COLLECTION_NAME).create(bData);

                    blockingVentDb.getCollection(COLLECTION_NAME).putValue(a, "accounts[0].balance", 1234);

                    EmbeddedReactiveQuery query = query(EqualsOperator.with("accounts[0].balance", 1234));
                    List<ObjectSnapshot> results = query.find(temporalService.now()).toStream().collect(toList());
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
                                lastUpdate(times.after(2)).
                                queryTime(times.after(3)).
                                version(1).build()
                        ),
                        results
                    );
                });
            }
        }


        @Test
        public void replaceListItemToMatchQuery() {
            temporalService.withResults(times.byInterval(4), () -> {
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
                VentId a = blockingVentDb.getCollection(COLLECTION_NAME).create(aData);
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
                VentId b = blockingVentDb.getCollection(COLLECTION_NAME).create(bData);

                blockingVentDb.getCollection(COLLECTION_NAME).putValue(a, "accounts[0]", map(pair("accountName", "AAA"), pair("balance", 314)));

                EmbeddedReactiveQuery query = query(EqualsOperator.with("accounts[0].balance", 314));
                List<ObjectSnapshot> results = query.find(temporalService.now()).toStream().collect(toList());
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
                            lastUpdate(times.after(2)).
                            queryTime(times.after(3)).
                            version(1).build()
                    ),
                    results
                );
            });
        }

        //fixme how does it work? PathUtils have a bug, so what the hell? oO
        @Test
        public void replaceListToMatchQuery() {
            temporalService.withResults(times.byInterval(4), () -> {
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
                VentId a = blockingVentDb.getCollection(COLLECTION_NAME).create(aData);
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
                VentId b = blockingVentDb.getCollection(COLLECTION_NAME).create(bData);

                blockingVentDb.getCollection(COLLECTION_NAME).putValue(a, "accounts", list(map(pair("accountName", "AAA"), pair("balance", 314))));

                EmbeddedReactiveQuery query = query(EqualsOperator.with("accounts[0].balance", 314));
                List<ObjectSnapshot> results = query.find(temporalService.now()).toStream().collect(toList());
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
                            lastUpdate(times.after(2)).
                            queryTime(times.after(3)).
                            version(1).build()
                    ),
                    results
                );
            });
        }
    }

    @Nested
    @DisplayName("Test higher level operators")
    public class HigherLevel {

        @Test
        @DisplayName("Query with negation")
        public void not() {
            temporalService.withResults(times.byInterval(3), () -> {
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
                VentId a = blockingVentDb.getCollection(COLLECTION_NAME).create(aData);
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
                VentId b = blockingVentDb.getCollection(COLLECTION_NAME).create(bData);

                EmbeddedReactiveQuery query = query(NotOperator.of(EqualsOperator.with("accounts[0].balance", 200)));
                List<ObjectSnapshot> results = query.find(temporalService.now()).toStream().collect(toList());
                assertEquals(
                    list(
                        ObjectSnapshot.builder().
                            ventId(a).
                            state(aData).
                            lastUpdate(times.after(0)).
                            queryTime(times.after(2)).
                            version(0).build()
                    ),
                    results
                );
            });
        }


        @Test
        @DisplayName("Query with conjunction")
        public void and() {
            temporalService.withResults(times.byInterval(5), () -> {
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
                VentId a = blockingVentDb.getCollection(COLLECTION_NAME).create(aData);
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
                VentId b = blockingVentDb.getCollection(COLLECTION_NAME).create(bData);
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
                VentId c = blockingVentDb.getCollection(COLLECTION_NAME).create(cData);
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
                VentId d = blockingVentDb.getCollection(COLLECTION_NAME).create(dData);

                EmbeddedReactiveQuery query = query(
                    AndOperator.builder().
                        operand(EqualsOperator.with("address.street", "S2")).
                        operand(EqualsOperator.with("name.last", "B2")).
                        build()

                );
                log.info("Query: "+query);
                Set<ObjectSnapshot> results = query.find(temporalService.now()).toStream().collect(toSet());
                assertEquals(
                    set(
                        ObjectSnapshot.builder().
                            ventId(b).
                            state(bData).
                            lastUpdate(times.after(1)).
                            queryTime(times.after(4)).
                            version(0).build(),
                        ObjectSnapshot.builder().
                            ventId(c).
                            state(cData).
                            lastUpdate(times.after(2)).
                            queryTime(times.after(4)).
                            version(0).build()
                    ),
                    results
                );
            });
        }

        @Test
        @DisplayName("Query with alternative")
        public void or() {
            temporalService.withResults(times.byInterval(5), () -> {
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
                VentId a = blockingVentDb.getCollection(COLLECTION_NAME).create(aData);
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
                VentId b = blockingVentDb.getCollection(COLLECTION_NAME).create(bData);
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
                VentId c = blockingVentDb.getCollection(COLLECTION_NAME).create(cData);
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
                VentId d = blockingVentDb.getCollection(COLLECTION_NAME).create(dData);

                EmbeddedReactiveQuery query = query(
                    OrOperator.builder().
                        operand(EqualsOperator.with("name.first", "B1")).
                        operand(EqualsOperator.with("name.last", "C2")).
                        build()

                );
                Set<ObjectSnapshot> results = query.find(temporalService.now()).toStream().collect(toSet());
                assertEquals(
                    set(
                        ObjectSnapshot.builder().
                            ventId(b).
                            state(bData).
                            lastUpdate(times.after(1)).
                            queryTime(times.after(4)).
                            version(0).build(),
                        ObjectSnapshot.builder().
                            ventId(c).
                            state(cData).
                            lastUpdate(times.after(2)).
                            queryTime(times.after(4)).
                            version(0).build()
                    ),
                    results
                );
            });
        }
    }

    //todo test update-related stuff

    private EmbeddedReactiveQuery query(Operator rootOperator){
        return new EmbeddedReactiveQuery(COLLECTION_NAME, rootOperator, mongoQueryPreparator, mongoTemplate, snapshotService);
    }

    private static Map person(String firstName, String lastName, int age, String street, String city, List<Map> accounts){
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

    private static Map account(String name, int balance){
        return map(
            pair("accountName", name),
            pair("balance", balance)
        );
    }
}