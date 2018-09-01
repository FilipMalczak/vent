package com.github.filipmalczak.vent.tck.query;

import com.github.filipmalczak.vent.api.blocking.BlockingVentDb;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveVentQuery;
import com.github.filipmalczak.vent.testing.ExpectedAndActualDiffExtension;
import com.github.filipmalczak.vent.testing.TestingTemporalService;
import com.github.filipmalczak.vent.testing.Times;
import com.github.filipmalczak.vent.traits.paradigm.Blocking;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.filipmalczak.vent.adapter.Adapters.adapt;
import static com.github.filipmalczak.vent.helper.Struct.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

//fixme these tests need a total refactor, but for now I'm just checking whether the approach will work at all
@Slf4j
@ExtendWith(ExpectedAndActualDiffExtension.class)
public abstract class VentQueryTck {
    private TestingTemporalService temporalService;
    private ReactiveVentDb<?, ?, ?> reactiveVentDb;

    private BlockingVentDb<?, ?, ?> blockingVentDb;

    private String collectionName = "collection"+Instant.now().toEpochMilli();
    private final static Times times = Times.defaultFromMilleniumBreak();

    // we could do this in tearDown/@AfterEach, but its better to clean the DB up before test,
    // so that we can investigate DB state post factum
    @BeforeEach
    public void setUp(){
        reactiveVentDb = provideClient();
        temporalService = provideTestingTemporalService();
        blockingVentDb = (BlockingVentDb) adapt(reactiveVentDb, Blocking.class);
//        temporalService.addResults(times.after(-3));
//        if (blockingVentDb.isManaged(collectionName)) {
//            temporalService.addResults(times.after(-2));
//            blockingVentDb.getCollection(collectionName).drop();
//        }
        temporalService.addResults(times.after(-1));
        blockingVentDb.manage(collectionName);
//        temporalService.clear();
    }

    //refactor setup of db and temporal service to AbstractTck
    protected abstract ReactiveVentDb provideClient();

    protected TestingTemporalService provideTestingTemporalService(){
        assumeTrue(reactiveVentDb.getTemporalService() instanceof TestingTemporalService);
        return (TestingTemporalService) reactiveVentDb.getTemporalService();
    }

    @Nested
    @DisplayName("Test behaviour of completely empty DB")
    public class EmptyDb {
        @Test
        @DisplayName("Query empty DB in reactive way")
        public void reactiveQuery(){
            temporalService.withResults(times.justNow(), () -> {
                StepVerifier.create(
                    reactiveVentDb.getCollection(collectionName).queryBuilder().equals("a", 1).build().
                        find(temporalService.now())
                ).verifyComplete();
            });
        }

        @Test
        @DisplayName("Query empty DB in adapters way")
        public void blockingQuery(){
            temporalService.withResults(times.justNow(), () -> {
                assertNull(
                    reactiveVentDb.getCollection(collectionName).queryBuilder().equals("a", 1).build().
                        find(temporalService.now()).
                        blockFirst()
                );
            });
        }
    }

    @Nested
    @DisplayName("Test equals operator")
    public class Equals {
        //todo: query by top level/nested int/float/bool/string field from initial state/after put/after update

        @Nested
        @DisplayName("Test querying of objects in their initial state")
        public class InitialState {
            @Test
            @DisplayName("Query by top level integer field")
            public void byTopLevelIntField() {
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
                    VentId a = blockingVentDb.getCollection(collectionName).create(aData);
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
                    VentId b = blockingVentDb.getCollection(collectionName).create(bData);

                    ReactiveVentQuery query = reactiveVentDb.getCollection(collectionName).queryBuilder().equals("age", 20).build();
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
            @DisplayName("Query by top level boolean field")
            public void byTopLevelBoolField() {
                temporalService.withResults(times.byInterval(3), () -> {
                    Map aData = map(
                        person(
                            "A1", "A2",
                            20,
                            "S1", "C1",
                            list(
                                account("AA1", 100),
                                account("AA2", 200),
                                account("AA3", 300)
                            )
                        ),
                        pair("happy", false)
                    );
                    VentId a = blockingVentDb.getCollection(collectionName).create(aData);
                    Map bData = map(
                        person(
                            "B1", "B2",
                            25,
                            "S2", "C1",
                            list(
                                account("AB1", 200),
                                account("AB2", 500),
                                account("AB3", 30)
                            )
                        ),
                        pair("happy", true)
                    );
                    VentId b = blockingVentDb.getCollection(collectionName).create(bData);

                    ReactiveVentQuery query = reactiveVentDb.getCollection(collectionName).queryBuilder().equals("happy", true).build();
                    List<ObjectSnapshot> results = query.find(temporalService.now()).toStream().collect(toList());
                    assertEquals(
                        list(
                            ObjectSnapshot.builder().
                                ventId(b).
                                state(bData).
                                lastUpdate(times.after(1)).
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
                    VentId a = blockingVentDb.getCollection(collectionName).create(aData);
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
                    VentId b = blockingVentDb.getCollection(collectionName).create(bData);

                    ReactiveVentQuery query = reactiveVentDb.getCollection(collectionName).queryBuilder().equals("name.last", "A2").build();
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
                    VentId a = blockingVentDb.getCollection(collectionName).create(aData);
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
                    VentId b = blockingVentDb.getCollection(collectionName).create(bData);

                    ReactiveVentQuery query = reactiveVentDb.getCollection(collectionName).queryBuilder().equals("accounts[0].balance", 100).build();
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

            @Nested
            @DisplayName("Events are PutValue")
            public class WithPutValue {
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
                        VentId a = blockingVentDb.getCollection(collectionName).create(aData);
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
                        VentId b = blockingVentDb.getCollection(collectionName).create(bData);

                        blockingVentDb.getCollection(collectionName).putValue(a, "age", 30);
                        blockingVentDb.getCollection(collectionName).putValue(b, "age", 35);

                        ReactiveVentQuery query = reactiveVentDb.getCollection(collectionName).queryBuilder().equals("age", 35).build();
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
                        VentId a = blockingVentDb.getCollection(collectionName).create(aData);
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
                        VentId b = blockingVentDb.getCollection(collectionName).create(bData);

                        blockingVentDb.getCollection(collectionName).putValue(a, "name.first", "X");
                        blockingVentDb.getCollection(collectionName).putValue(b, "name.first", "Y");

                        ReactiveVentQuery query = reactiveVentDb.getCollection(collectionName).queryBuilder().equals("name.first", "Y").build();
                        List<ObjectSnapshot> results = query.find(temporalService.now()).toStream().collect(toList());
                        assertEquals(
                            list(
                                ObjectSnapshot.builder().
                                    ventId(b).
                                    state(map(
                                        bData,
                                        pair("name", map(
                                            pair("first", "Y"),
                                            pair("last", ((Map<String, ?>) bData.get("name")).get("last"))
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
                        VentId a = blockingVentDb.getCollection(collectionName).create(aData);
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
                        VentId b = blockingVentDb.getCollection(collectionName).create(bData);

                        blockingVentDb.getCollection(collectionName).
                            putValue(a, "name", map(
                                pair("first", "X1"),
                                pair("last", "X2")
                            ));
                        blockingVentDb.getCollection(collectionName).
                            putValue(b, "name", map(
                                pair("first", "Y1"),
                                pair("last", "Y2")
                            ));

                        ReactiveVentQuery query = reactiveVentDb.getCollection(collectionName).queryBuilder().equals("name.last", "X2").build();
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
                        VentId a = blockingVentDb.getCollection(collectionName).create(aData);
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
                        VentId b = blockingVentDb.getCollection(collectionName).create(bData);

                        blockingVentDb.getCollection(collectionName).putValue(a, "accounts[0].balance", 1234);

                        ReactiveVentQuery query = reactiveVentDb.getCollection(collectionName).queryBuilder().equals("accounts[0].balance", 1234).build();
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


            @Nested
            @DisplayName("Events are Update")
            public class UpdateRelated {
                //todo more extensive tests of update; see c.g.f.v.tck.VentDbTck

                @Test
                public void createUpdate(){
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
                        VentId a = blockingVentDb.getCollection(collectionName).create(aData);
                        Map aUpdatedData = map(aData, pair("age", 30));
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
                        Map bUpdatedData = map(bData, pair("age", 35));
                        VentId b = blockingVentDb.getCollection(collectionName).create(bData);

                        blockingVentDb.getCollection(collectionName).putValue(a, "age", 30);
                        blockingVentDb.getCollection(collectionName).putValue(b, "age", 35);

                        ReactiveVentQuery query = reactiveVentDb.getCollection(collectionName).queryBuilder().equals("age", 35).build();
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
                VentId a = blockingVentDb.getCollection(collectionName).create(aData);
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
                VentId b = blockingVentDb.getCollection(collectionName).create(bData);

                blockingVentDb.getCollection(collectionName).putValue(a, "accounts[0]", map(pair("accountName", "AAA"), pair("balance", 314)));

                ReactiveVentQuery query = reactiveVentDb.getCollection(collectionName).queryBuilder().equals("accounts[0].balance", 314).build();
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
                VentId a = blockingVentDb.getCollection(collectionName).create(aData);
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
                VentId b = blockingVentDb.getCollection(collectionName).create(bData);

                blockingVentDb.getCollection(collectionName).putValue(a, "accounts", list(map(pair("accountName", "AAA"), pair("balance", 314))));

                ReactiveVentQuery query = reactiveVentDb.getCollection(collectionName).queryBuilder().equals("accounts[0].balance", 314).build();
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
                VentId a = blockingVentDb.getCollection(collectionName).create(aData);
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
                VentId b = blockingVentDb.getCollection(collectionName).create(bData);

                ReactiveVentQuery query = reactiveVentDb.getCollection(collectionName).queryBuilder().not(c -> c.equals("accounts[0].balance", 200)).build();
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
                VentId a = blockingVentDb.getCollection(collectionName).create(aData);
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
                VentId b = blockingVentDb.getCollection(collectionName).create(bData);
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
                VentId c = blockingVentDb.getCollection(collectionName).create(cData);
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
                VentId d = blockingVentDb.getCollection(collectionName).create(dData);

                ReactiveVentQuery query = reactiveVentDb.getCollection(collectionName).queryBuilder().and(cb ->
                    cb.
                        equals("address.street", "S2").
                        equals("name.last", "B2")
                ).build();
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
                VentId a = blockingVentDb.getCollection(collectionName).create(aData);
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
                VentId b = blockingVentDb.getCollection(collectionName).create(bData);
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
                VentId c = blockingVentDb.getCollection(collectionName).create(cData);
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
                VentId d = blockingVentDb.getCollection(collectionName).create(dData);

                ReactiveVentQuery query = reactiveVentDb.getCollection(collectionName).queryBuilder().or(cb ->
                    cb.
                        equals("name.first", "B1").
                        equals("name.last", "C2")
                ).build();
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