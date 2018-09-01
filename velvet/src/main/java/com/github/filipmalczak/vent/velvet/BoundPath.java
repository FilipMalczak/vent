package com.github.filipmalczak.vent.velvet;

import java.util.function.Function;

public interface BoundPath {
    String getPath();

    Object getTarget();

    boolean exists();

    void set(Object value);

    Object get();

    default Object modify(Function function){
        Object newVal = function.apply(get());
        set(newVal);
        return newVal;
    }

    void delete();
}
