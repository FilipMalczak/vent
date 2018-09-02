package com.github.filipmalczak.vent.mongo.service.query.preparator;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class IndexingFromBracketsToDots implements Traversal {
    @Override
    public Map processMap(Map arg) {
        Map result = new HashMap();
        for (Object key: arg.keySet())
            result.put(normalizePath(key), process(arg.get(key)));
        return result;
    }

    private Object normalizePath(Object key){
        if (key instanceof String){
            String result = ((String) key).replaceAll("\\[", ".").replaceAll("\\]", "");
            return result;
        }
        return key;
    }
}
