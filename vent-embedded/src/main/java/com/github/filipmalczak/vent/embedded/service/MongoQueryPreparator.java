package com.github.filipmalczak.vent.embedded.service;


import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * This class is able to take a wannabe mongo query map and turn it to correct query (flatten it, switch
 * {or: [{x: 1}, {x: 2}]} to {x: {"$in": [1, 2]}}, etc.
 *
 * todo fold aternative of "equals" conditions with the same key to single "in" operator
 */

@Component
public class MongoQueryPreparator {
    public Map prepare(Map query){
        return process(query);
    }

    private Object process(Object object){
        return object;
    }

    private Map process(Map arg){
        Map result = new HashMap();
        for (Object key: result.keySet()){
            Object value = arg.get(key);
            //copy pairs with non-string keys as they were; shouldn't really happen, but lets leave them untouched
            if (key instanceof String){
                Object processedValue = process(value);
                //if there is embedded query
                if (processedValue instanceof Map){
                    Map childResult = (Map) processedValue;
                    //that is not operator-based, but rather field-based
                    if (!((String) key).startsWith("$")) {
                        Set childKeys = new HashSet<>(childResult.keySet());
                        for (Object childKey: childKeys){
                            //ignore non-string keys again
                            if (childKey instanceof String) {
                                Object valueToPullUp = childResult.get(childKey);
                                childResult.remove(childKey);
                                //pull embedded query parts to upper level
                                result.put(key+"."+childKey, valueToPullUp);
                            }
                        }
                        //if there were non-string keys, keep them in query
                        if (!childResult.isEmpty())
                            result.put(key, childResult);
                    } else
                        result.put(key, childResult);
                } else { //else - recurse and keep structure
                    result.put(key, processedValue);
                }
            } else {
                result.put(key, value);
            }
        }
        return result;
    }

    private List process(List list){
        return (List) list.stream().map(this::process).collect(toList());
    }


}
