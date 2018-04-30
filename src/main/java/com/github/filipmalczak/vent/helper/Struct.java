package com.github.filipmalczak.vent.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class Struct {
    private Struct(){}

    public static Map pair(Object k, Object v){
        Map out = new HashMap();
        out.put(k, v);
        return out;
    }

    public static Map map(Map... maps){
        Map out = new HashMap();
        for (Map m: maps)
            for (Object k: m.keySet())
                out.put(k, m.get(k));
        return out;
    }

    public static List list(Object... vals){
        return asList(vals);
    }
    
}
