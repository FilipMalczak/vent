package com.github.filipmalczak.vent.mongo.service.query.preparator;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static com.github.filipmalczak.vent.helper.Struct.list;

/**
 * This class is able to take a wannabe mongo query map and turn it to correct query (flatten it, switch
 * {or: [{x: 1}, {x: 2}]} to {x: {"$in": [1, 2]}}, etc.
 *
 * todo fold aternative of "equals" conditions with the same key to single "in" operator
 */
@Slf4j
public class MongoQueryPreparator {

    private static List<Traversal> recurringTraversals = list(
        new FlattenPaths(),
        new CollapseOrOperator(),
        new PullOrOperatorUp(),
        new PullNotOperatorUp(),
        new NotOrToNor(),
        new NotEqualsToNe()
    );
    private static List<Traversal> singleShotTraversals = list(new IndexingFromBracketsToDots());

    @SneakyThrows
    public Map prepare(Map query){
        log.debug("Unprepared query: {}", query);
        Map result = processMap(query);
        log.debug("Prepared query: {}", result);
        return result;
    }

    private Map applyTraversals(Map arg, List<Traversal> traversals){
        Map result = arg;
        for (Traversal traversal: traversals)
            result = traversal.processMap(result);
        return result;
    }

    @SneakyThrows
    private Map processMap(Map arg){
        int i = 0;
        Map result = arg;
        int previousHashCode = result.hashCode()+1;
        while (result.hashCode() != previousHashCode) {
            log.debug("Processing #"+(i++)+" ; #result="+(result.hashCode())+"; #prev="+previousHashCode);
            previousHashCode = result.hashCode();
            result = applyTraversals(result, recurringTraversals);
            log.debug("Processing #"+(i++)+"; result: "+result);
        }
        result = applyTraversals(result, singleShotTraversals);
        return result;
    }
}
