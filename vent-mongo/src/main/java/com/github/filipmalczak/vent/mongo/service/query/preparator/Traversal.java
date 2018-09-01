package com.github.filipmalczak.vent.mongo.service.query.preparator;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public interface Traversal {
    default Object process(Object object){
        if (object instanceof Map)
            return processMap((Map) object);
        if (object instanceof List)
            return processList((List) object);

        return object;
    }

    Map processMap(Map arg);

    default List processList(List list){
        return (List) list.stream().map(this::process).collect(toList());
    }
}
