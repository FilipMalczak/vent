package com.github.filipmalczak.vent.mongo.service.query.preparator;

import java.util.HashMap;
import java.util.Map;

import static com.github.filipmalczak.vent.helper.Struct.pair;
import static com.github.filipmalczak.vent.mongo.service.query.preparator.OperatorsConstants.*;

public class NotEqualsToNe implements Traversal {
    @Override
    public Map processMap(Map arg) {
        if (arg.keySet().contains(notOperator) &&
            arg.get(notOperator) instanceof Map &&
            ((Map)(arg).get(notOperator)).keySet().stream().allMatch(k -> k instanceof String && isObjectPath((String) k))) {
            Map result = new HashMap();
            for (Object key: arg.keySet())
                if (!key.equals(notOperator))
                    result.put(key, arg.get(key));
            for (String key: ((Map<String, ?>)(arg).get(notOperator)).keySet())
                result.put(key, pair(neOperator, process(((Map) arg.get(notOperator)).get(key))));
            return result;
        }
        Map result = new HashMap();
        for (Object key: arg.keySet())
            result.put(key, process(arg.get(key)));
        return result;
    }
}
