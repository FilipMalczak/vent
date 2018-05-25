package com.github.filipmalczak.vent.helper;

import java.util.*;

import static java.util.Arrays.asList;

public class Struct {
    private Struct(){}

    public static Map pair(Object k, Object v){
        Map out = new HashMap();
        out.put(k, v);
        return out;
    }

    public static Map map(List<Map> maps){
        return map(maps.toArray(new Map[0]));
    }

    public static Map map(Map... maps){
        Map out = new HashMap();
        for (Map m: maps)
            for (Object k: m.keySet())
                out.put(k, m.get(k));
        return out;
    }

    public static Set set(Object... vals){
        return new HashSet(list(vals));
    }

    public static List list(Object... vals){
        return asList(vals);
    }
    
}
