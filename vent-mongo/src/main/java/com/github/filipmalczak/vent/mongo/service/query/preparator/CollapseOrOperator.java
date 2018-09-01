package com.github.filipmalczak.vent.mongo.service.query.preparator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.filipmalczak.vent.mongo.service.query.preparator.OperatorsConstants.collapsableOperators;

public class CollapseOrOperator implements Traversal {
    @Override
    public Map processMap(Map arg) {
        Map result = new HashMap();
        for (Object key: arg.keySet()){
            Object processedValue = process(arg.get(key));
            if (collapsableOperators.contains(key)){
                //assume that value is List and values are maps; todo proper exception
                List<Map> alternatives = (List<Map>) processedValue;
                List<Map> processed = new LinkedList<>();
                for (Map alternativeLeg: alternatives){
                    if (alternativeLeg.containsKey(key)) {
                        //same here; todo
                        List<Map> subAlternatives = (List<Map>) alternativeLeg.get(key);
                        for (Map subAlternative: subAlternatives){
                            for (Object legKey: alternativeLeg.keySet()){
                                if (!legKey.equals(key))
                                    subAlternative.put(legKey, alternativeLeg.get(legKey));
                            }
                            processed.add(subAlternative);
                        }
                    } else
                        processed.add(alternativeLeg);
                }
                processed = processed.stream().distinct().collect(Collectors.toList());
                processedValue = processed;
            }
            result.put(key, processedValue);
        }
        return result;
    }
}
