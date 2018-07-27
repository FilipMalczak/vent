package com.github.filipmalczak.vent.helper;

import com.rits.cloning.Cloner;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Cloning {
    private static final Cloner CLONER = new Cloner();

    public static <T> T deepClone(T val){
        return CLONER.deepClone(val);
    }
}
