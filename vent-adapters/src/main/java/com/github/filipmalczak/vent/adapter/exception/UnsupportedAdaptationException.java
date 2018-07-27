package com.github.filipmalczak.vent.adapter.exception;

import com.github.filipmalczak.vent.adapter.Adaptation;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UnsupportedAdaptationException extends AdaptationException {
    private final Adaptation adaptation;

    public UnsupportedAdaptationException(Adaptation adaptation) {
        super("No adapter supporting adaptation "+adaptation+" =is available!");
        this.adaptation = adaptation;
    }
}
