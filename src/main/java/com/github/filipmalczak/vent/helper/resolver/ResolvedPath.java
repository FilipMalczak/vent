package com.github.filipmalczak.vent.helper.resolver;

import java.util.List;
import java.util.Map;

import static com.github.filipmalczak.vent.helper.resolver.Helper.resolvePathPart;

public interface ResolvedPath {
    boolean exists();
    Object get();
    void set(Object o);
    void delete();

    default ResolvedPath resolveIndex(int i) {
        Object o = get();
        if (o instanceof List)
            return AtListIndex.from((List) o, i);
        throw new UnsupportedOperationException("Indexing possible only for lists!");
    }

    default ResolvedPath resolveMember(String name) {
        Object o = get();
        if (o instanceof Map)
            return MemberOf.from((Map) o, name);
        throw new UnsupportedOperationException("Member access possible only for maps!");
    }

    default ResolvedPath resolve(String path){
        int firstDotIdx = path.indexOf('.');
        String firstPart = firstDotIdx > 0 ? path.substring(0, firstDotIdx) : path;
        Object o = get();
        if (!(o instanceof Map))
            throw new UnsupportedOperationException("Paths resolving possible only for maps!");
        ResolvedPath result = resolvePathPart((Map) o, firstPart);
        return firstDotIdx > 0 ? result.resolve(path.substring(firstDotIdx+1)) : result;
    }
}
