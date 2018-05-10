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

    private Object processAnything(Object object){
        if (object instanceof Map)
            return process((Map) object);
        if (object instanceof List)
            return process((List) object);
        return object;
    }

    private Map process(Map arg){
        Map result = new HashMap();
        for (Object key: result.keySet()){
            Object value = arg.get(key);
            if (key instanceof String){
                Object processedValue = processAnything(value);
                if (processedValue instanceof Map){
                    Map childResult = (Map) processedValue;
                    if (!((String) key).startsWith("$")) {
                        Set childKeys = new HashSet<>(childResult.keySet());
                        for (Object childKey: childKeys){
                            if (childKey instanceof String) {
                                Object valueToPullUp = childResult.get(childKey);
                                childResult.remove(childKey);
                                result.put(key+"."+childKey, valueToPullUp);
                            }
                        }
                        if (!childResult.isEmpty())
                            result.put(key, childResult);
                    } else
                        result.put(key, childResult);
                } else {
                    result.put(key, processedValue);
                }
            } else {
                result.put(key, value);
            }
        }
        return result;
    }

    private List process(List list){
        return (List) list.stream().map(this::processAnything).collect(toList());
    }


}
