package com.github.filipmalczak.vent.mongo.service.query.preparator;

import java.util.HashMap;
import java.util.Map;

import static com.github.filipmalczak.vent.helper.Struct.pair;
import static com.github.filipmalczak.vent.helper.Struct.set;
import static com.github.filipmalczak.vent.mongo.service.query.preparator.OperatorsConstants.*;

public class NotOrToOr implements Traversal {
    @Override
    public Map processMap(Map arg) {
        if (arg.keySet().equals(set(notOperator)) &&
            arg.get(notOperator) instanceof Map &&
            ((Map)(arg).get(notOperator)).keySet().equals(set(orOperator))) {
            //todo this casting for value
            return pair(norOperator, processMap((Map)(arg).get(notOperator)).get(orOperator));
        }
        Map result = new HashMap();
        for (Object key: arg.keySet())
            result.put(key, process(arg.get(key)));
        return result;
    }


}
