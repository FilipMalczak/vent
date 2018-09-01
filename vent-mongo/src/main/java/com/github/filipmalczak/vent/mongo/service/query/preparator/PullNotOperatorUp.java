package com.github.filipmalczak.vent.mongo.service.query.preparator;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static com.github.filipmalczak.vent.mongo.service.query.preparator.OperatorsConstants.isObjectPath;
import static com.github.filipmalczak.vent.mongo.service.query.preparator.OperatorsConstants.notOperator;

@Slf4j
public class PullNotOperatorUp implements Traversal {

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
                            if (operator.equals(notOperator)) {
                                //assumed that value is map; todo
                                Map negated = (Map) mapValue.get(operator);
                                if (negated.keySet().stream().allMatch(v -> v instanceof String && isObjectPath((String) v))) {
                                    Map prefixed = new HashMap();
                                    for (Object withoutPrefixKey : negated.keySet()) {
                                        String withoutPrefixKeyString = (String) withoutPrefixKey;
                                        prefixed.put(stringKey + "." + withoutPrefixKeyString, negated.get(withoutPrefixKey));
                                    }
                                    result.put(operator, prefixed);
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
