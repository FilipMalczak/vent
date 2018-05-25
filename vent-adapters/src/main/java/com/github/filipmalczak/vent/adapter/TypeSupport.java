package com.github.filipmalczak.vent.adapter;

public enum TypeSupport {
    IMPLEMENTATION,
    INTERFACE,
    TRAIT,
    NONE;

    public AdaptationSupport with(TypeSupport targetSupport){
        return AdaptationSupport.of(this, targetSupport);
    }
}
