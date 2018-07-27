package com.github.filipmalczak.vent.adapter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TypeUtils {
    public static boolean firstExtendsSecond(Class first, Class second){
        return second.isAssignableFrom(first);
    }
}
