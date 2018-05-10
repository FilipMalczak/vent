package com.github.filipmalczak.vent.helper;

import com.rits.cloning.Cloner;

public class Cloning {
    private Cloning(){}

    private static final Cloner CLONER = new Cloner();

    public static <T> T deepClone(T val){
        return CLONER.deepClone(val);
    }
}
