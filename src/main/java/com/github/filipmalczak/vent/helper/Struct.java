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

    @Deprecated
    //this is used only in tests - this additional frame of unpacking list to array may be small, but it can matter
    //in production
    //its not really depreacted, but rather marked as such so it won't get used in main code by mistake
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

    public static List list(Object... vals){
        return asList(vals);
    }
    
}
