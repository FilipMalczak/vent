package com.github.filipmalczak.vent.service.impl;

import com.github.filipmalczak.vent.VentSpringTest;
import com.github.filipmalczak.vent.model.Vent;
import com.github.filipmalczak.vent.model.VentObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.filipmalczak.vent.dto.Operation.*;
import static com.github.filipmalczak.vent.helper.Struct.*;
import static java.util.Arrays.asList;
import static reactor.core.publisher.Mono.just;

@VentSpringTest
class SimpleVentingServiceTest {
    @Autowired
    private SimpleVentingService service;

    private Map emptyState;
    private Map nonEmptyState;

    @BeforeEach
    public void setUp(){
        emptyState = map();
        nonEmptyState = map(
            pair("a", map(
                pair("b", list(1, 2, 3)),
                pair("c", false)
            )),
            pair("d", "d"),
            pair("e", true)
        );
    }

    private void assertResultState(Map initialState, List<Vent> vents, Map expected){
        StepVerifier.create(
            service.applyVents(
                just(
                    new VentObject(
                        null,
                        initialState,
                        vents,
                        null
                    )
                )
            )
        ).expectNext(
            expected
        );
    }


    @Test
    public void noVentsWithEmptyObject(){
        assertResultState(emptyState, list(), emptyState);
    }

    @Test
    public void noVentsWithNonEmptyObject(){
        assertResultState(nonEmptyState, list(), nonEmptyState);
    }

    @Test
    public void singleSetWithEmptyObject(){
        String path = "aPath";
        String value = "aValue";
        assertResultState(
            emptyState,
            list(
                new Vent(
                    SET,
                    map(
                        pair("path", path),
                        pair("value", value)
                    ),
                    null
                )
            ),
            pair(path, value)
        );
    }

    @Test
    public void simplePathSetWithNonEmptyObject(){
        String path = "d";
        String value = "d2";
        assertResultState(
            nonEmptyState,
            list(
                new Vent(
                    SET,
                    map(
                        pair("path", path),
                        pair("value", value)
                    ),
                    null
                )
            ),
            map(
                pair("a", map(
                    pair("b", list(1, 2, 3)),
                    pair("c", false)
                )),
                pair("d", value),
                pair("e", true)
            )
        );
    }

    @Test
    public void deepPathSetWithNonEmptyObject(){
        String path = "a.b[1]";
        int value = 5;
        assertResultState(
            nonEmptyState,
            list(
                new Vent(
                    SET,
                    map(
                        pair("path", path),
                        pair("value", value)
                    ),
                    null
                )
            ),
            map(
                pair("a", map(
                    pair("b", list(1, value, 3)),
                    pair("c", false)
                )),
                pair("d", "d"),
                pair("e", true)
            )
        );
    }

    @Test
    public void addToList(){
        String path = "a.b";
        int value = 5;
        assertResultState(
            nonEmptyState,
            list(
                new Vent(
                    ADD,
                    map(
                        pair("path", path),
                        pair("value", value)
                    ),
                    null
                )
            ),
            map(
                pair("a", map(
                    pair("b", list(1, 2, 3, value)),
                    pair("c", false)
                )),
                pair("d", "d"),
                pair("e", true)
            )
        );
    }

    @Test
    public void removeByMember(){
        String path = "e";
        assertResultState(
            nonEmptyState,
            list(
                new Vent(
                    ADD,
                    pair("path", path),
                    null
                )
            ),
            map(
                pair("a", map(
                    pair("b", list(1, 2, 3)),
                    pair("c", false)
                )),
                pair("d", "d")
            )
        );
    }

    @Test
    public void removeByIndex(){
        String path = "a.b[1]";
        assertResultState(
            nonEmptyState,
            list(
                new Vent(
                    ADD,
                    pair("path", path),
                    null
                )
            ),
            map(
                pair("a", map(
                    pair("b", list(1, 3)),
                    pair("c", false)
                )),
                pair("d", "d"),
                pair("e", true)
            )
        );
    }

    @Test
    public void exampleObjectEvolution(){
        assertResultState(
            nonEmptyState,
            list(
                new Vent(SET, map(pair("path", "x"), pair("value", "X")), null),
                new Vent(ADD, map(pair("path", "a.b"), pair("value", 42)), null),
                new Vent(ADD, map(pair("path", "a.b"), pair("value", 696)), null),
                new Vent(SET, map(pair("path", "a.b[3]"), pair("value", 88)), null),
                new Vent(REMOVE, pair("path", "d"), null)
            ),
            map(
                pair("a", map(
                    pair("b", list(1, 2, 3, 88, 696)),
                    pair("c", false)
                )),
                pair("e", true)
            )
        );
    }

    //todo tesst following cases:
    //delete
    //delete and create (no init state and init state)
    //put with non empty
    //put with empty
}