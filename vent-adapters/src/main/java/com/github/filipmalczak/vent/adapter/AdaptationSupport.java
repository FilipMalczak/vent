package com.github.filipmalczak.vent.adapter;

import lombok.Value;

import java.util.Comparator;

@Value(staticConstructor = "of")
public class AdaptationSupport implements Comparable<AdaptationSupport> {
    private TypeSupport sourceSupport;
    private TypeSupport targetSupport;

    public boolean isSupported(){
        return sourceSupport != TypeSupport.NONE && targetSupport != TypeSupport.NONE;
    }

    @Override
    public int compareTo(AdaptationSupport o) {
        return Comparator.
            comparing(AdaptationSupport::getSourceSupport).
            thenComparing(AdaptationSupport::getTargetSupport).
            compare(this, o);
    }
}
