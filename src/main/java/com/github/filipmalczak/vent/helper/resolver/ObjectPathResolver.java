package com.github.filipmalczak.vent.helper.resolver;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ObjectPathResolver {
    //todo: add negative indexing, python-like
    public ResolvedPath resolve(Object target, String path){
        Map root = new HashMap();
        String key = "__root_dummy_key";
        root.put(key, target);
        return MemberOf.from(root, key).resolve(path);
    }
}
