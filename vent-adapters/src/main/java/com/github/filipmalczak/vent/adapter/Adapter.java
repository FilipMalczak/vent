package com.github.filipmalczak.vent.adapter;

public interface Adapter {
    AdaptationSupport supports(Adaptation adaptation);
    // todo some customization mechanism is needed, e.g. to specify number of workers when adapting something to async
    // idea: system properties or Map<String, Object> as additional parameter to adapt(S, Class<T>) - but this is
    // ugly, because we shouldn't know anything about loaded services

    public <T, S> T adapt(S source, Class<T> targetClass);

    //fixme screwed up visibility here
    static boolean firstExtendsSecond(Class first, Class second){
        return second.isAssignableFrom(first);
    }
}
