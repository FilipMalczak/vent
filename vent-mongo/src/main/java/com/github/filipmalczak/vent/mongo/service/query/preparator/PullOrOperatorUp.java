package com.github.filipmalczak.vent.mongo.service.query.preparator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.filipmalczak.vent.mongo.service.query.preparator.OperatorsConstants.isObjectPath;
import static com.github.filipmalczak.vent.mongo.service.query.preparator.OperatorsConstants.orOperator;
import static java.util.stream.Collectors.toList;

public class PullOrOperatorUp implements Traversal {
    @Override
    public Map processMap(Map arg) {
        Map result = new HashMap();
        for (Object key: arg.keySet()) {
            Object processedValue = process(arg.get(key));
            if (key instanceof String) {
                String stringKey = (String) key;
                if (isObjectPath(stringKey)) {
                    if (processedValue instanceof Map) {
                        Map mapValue = (Map) processedValue;
                        if (mapValue.size() == 1) {
                            Object operator = mapValue.keySet().iterator().next();
                            if (operator.equals(orOperator)) {
                                //assumed that value is list; todo
                                List alternatives = (List) mapValue.get(operator);
                                if (alternatives.stream().allMatch(v -> v instanceof Map &&
                                    ((Map)v).keySet().stream().allMatch(v2 -> v2 instanceof String && isObjectPath((String) v2)))) {
                                    result.put(operator, ((List)mapValue.values().iterator().next()).stream().map(withoutPrefix -> {
                                        Map prefixed = new HashMap();
                                        for (String withoutPrefixKey : ((Map<String, ?>) withoutPrefix).keySet()) {
                                            prefixed.put(stringKey + "." + withoutPrefixKey, ((Map) withoutPrefix).get(withoutPrefixKey));
                                        }
                                        return prefixed;
                                    }).collect(toList()));
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
            result.put(key, processedValue);
        }
        return result;
    }
}
