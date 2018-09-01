package com.github.filipmalczak.vent.mongo.service.query.preparator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FlattenPaths implements Traversal {
    @Override
    public Map processMap(Map arg) {
        Map result = new HashMap();
        for (Object key: arg.keySet()){
            Object value = arg.get(key);
            //copy pairs with non-string keys as they were; shouldn't really happen, but lets leave them untouched
            if (key instanceof String){
                Object processedValue = process(value);
                //if there is mongo query object
                if (processedValue instanceof Map){
                    Map childResult = (Map) processedValue;
                    //that is not operator-based, but rather field-based
                    if (!((String) key).startsWith("$")) {
                        Set childKeys = new HashSet<>(childResult.keySet());
                        for (Object childKey: childKeys){
                            //ignore non-string and operator-based keys again
                            if (childKey instanceof String && ! ((String) childKey).startsWith("$")) {
                                Object valueToPullUp = childResult.get(childKey);
                                childResult.remove(childKey);
                                //pull mongo query parts to upper level
                                result.put(key+"."+childKey, valueToPullUp);
                            }
                            //no else, because we'll add non-string keys in a moment
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
                result.put(key, process(value));
            }
        }
        return result;
    }
}
