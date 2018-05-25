package com.github.filipmalczak.vent.adapter;

public enum TypeSupport {
    IMPLEMENTATION,
    INTERFACE,
    TRAIT,
    ANYTHING,
    NONE;

    public AdaptationSupport with(TypeSupport targetSupport){
        return AdaptationSupport.of(this, targetSupport);
    }

    public TypeSupport against(TypeSupport other){
        return this.compareTo(other) < 0 ? other : this;
    }
}
