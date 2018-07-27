package com.github.filipmalczak.vent.adapter.exception;

import com.github.filipmalczak.vent.adapter.Adaptation;
import com.github.filipmalczak.vent.adapter.AdaptationSupport;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AmbiguousAdaptationException extends AdaptationException {
    private final Adaptation adaptation;
    private final AdaptationSupport adaptationSupport;

    public AmbiguousAdaptationException(Adaptation adaptation, AdaptationSupport adaptationSupport) {
        super("There is more than one adapter that supports adaptation "+adaptation+" on "+adaptationSupport+" level");
        this.adaptation = adaptation;
        this.adaptationSupport = adaptationSupport;
    }
}
