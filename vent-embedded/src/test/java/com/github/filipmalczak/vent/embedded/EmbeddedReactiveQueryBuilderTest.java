package com.github.filipmalczak.vent.embedded;

import com.github.filipmalczak.vent.TestConfiguration;
import com.github.filipmalczak.vent.VentSpringTest;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.embedded.model.events.impl.EventFactory;
import com.github.filipmalczak.vent.embedded.query.EmbeddedReactiveQuery;
import com.github.filipmalczak.vent.embedded.query.operator.*;
import com.github.filipmalczak.vent.embedded.service.CollectionService;
import com.github.filipmalczak.vent.embedded.service.MongoQueryPreparator;
import com.github.filipmalczak.vent.embedded.service.PageService;
import com.github.filipmalczak.vent.embedded.service.SnapshotService;
import com.github.filipmalczak.vent.testing.TestingTemporalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static reactor.core.publisher.Mono.just;

@SpringJUnitConfig({TestConfiguration.class, EmbeddedWithSpringDataConfiguration.class})
class EmbeddedReactiveQueryBuilderTest {
    @Mock
    private PageService pageService;
    //this is unused explicitly, but is needed so that mockito can use it when creating ventDb field
    @Mock
    private EventFactory eventFactory;
    @Mock
    private MongoQueryPreparator mongoQueryPreparator;
    @Mock
    private ReactiveMongoTemplate mongoTemplate;
    @Mock
    private SnapshotService snapshotService;
    @Mock
    private CollectionService collectionService;

    @Autowired
    private TestingTemporalService temporalService;

    private static final String COLLECTION_NAME = "collection";

    @InjectMocks
    private EmbeddedReactiveVentDb ventDb;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        //todo this can be extracted; "passes initialization" fixture can be useful
        when(collectionService.manage(COLLECTION_NAME)).thenReturn(just(Success.NO_OP_SUCCESS));
        when(pageService.getTemporalService()).thenReturn(temporalService);
    }

    @Test
    public void testSimpleEquals(){
        assertEquals(
            query(EqualsOperator.with("x", 1)),
            ventDb.getCollection(COLLECTION_NAME).queryBuilder().
                equals("x", 1).
                build()
        );
    }

    @Test
    public void testSingleChildAnd(){
        assertEquals(
            query(EqualsOperator.with("x", 1)),
            ventDb.getCollection(COLLECTION_NAME).queryBuilder().
                and(c ->
                    c.equals("x", 1)
                ).
                build()
        );
    }

    @Test
    public void testSingleChildOr(){
        assertEquals(
            query(EqualsOperator.with("x", 1)),
            ventDb.getCollection(COLLECTION_NAME).queryBuilder().
                or(c ->
                    c.equals("x", 1)
                ).
                build()
        );
    }

    @Test
    public void testSeveralChildrenAnd(){
        assertEquals(
            query(
                AndOperator.builder().
                    operand(EqualsOperator.with("x", 1)).
                    operand(EqualsOperator.with("y", "z")).
                    build()
            ),
            ventDb.getCollection(COLLECTION_NAME).queryBuilder().
                and(c ->
                    c.equals("x", 1).equals("y", "z")
                ).
                build()
        );
    }

    @Test
    public void testSeveralChildrenOr(){
        assertEquals(
            query(
                OrOperator.builder().
                    operand(EqualsOperator.with("x", 1)).
                    operand(EqualsOperator.with("y", "z")).
                    build()
            ),
            ventDb.getCollection(COLLECTION_NAME).queryBuilder().
                or(c ->
                    c.equals("x", 1).equals("y", "z")
                ).
                build()
        );
    }

    @Test
    public void testAndFlattening(){
        assertEquals(
            query(
                AndOperator.builder().
                    operand(EqualsOperator.with("x", 1)).
                    operand(EqualsOperator.with("y", "z")).
                    operand(EqualsOperator.with("w", true)).
                    build()
            ),
            ventDb.getCollection(COLLECTION_NAME).queryBuilder().
                and(c ->
                    c.equals("x", 1).
                    and(c2 ->
                        c2.equals("y", "z").
                        and(c3 ->
                            c3.equals("w", true)
                        )
                    )
                ).
                build()
        );
    }

    @Test
    public void testOrFlattening(){
        assertEquals(
            query(
                OrOperator.builder().
                    operand(EqualsOperator.with("x", 1)).
                    operand(EqualsOperator.with("y", "z")).
                    operand(EqualsOperator.with("w", true)).
                    build()
            ),
            ventDb.getCollection(COLLECTION_NAME).queryBuilder().
                or(c ->
                    c.equals("x", 1).
                    or(c2 ->
                        c2.equals("y", "z").
                        or(c3 ->
                            c3.equals("w", true)
                        )
                    )
                ).
                build()
        );
    }

    @Test
    public void testNotEquals(){
        assertEquals(
            query(
                NotOperator.of(EqualsOperator.with("x", 1))
            ),
            ventDb.getCollection(COLLECTION_NAME).queryBuilder().
                not(
                    c -> c.equals("x", 1)
                ).
                build()
        );
    }

    @Test
    public void testNotNot(){
        assertEquals(
            query(
                EqualsOperator.with("x", 1)
            ),
            ventDb.getCollection(COLLECTION_NAME).queryBuilder().
                not(
                    c -> c.not(c2 -> c2.equals("x", 1))
                ).
                build()
        );
    }

    @Test
    public void testNotNotNot(){
        assertEquals(
            query(
                NotOperator.of(EqualsOperator.with("x", 1))
            ),
            ventDb.getCollection(COLLECTION_NAME).queryBuilder().
                not(
                    c -> c.not(c2 -> c2.not(c3 -> c3.equals("x", 1)))
                ).
                build()
        );
    }

    private EmbeddedReactiveQuery query(Operator rootOperator){
        return new EmbeddedReactiveQuery(COLLECTION_NAME, rootOperator, mongoQueryPreparator, mongoTemplate, snapshotService, collectionService, ventDb.getTemporalService());
    }
}