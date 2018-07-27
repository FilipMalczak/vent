package com.github.filipmalczak.vent.adapter;

import com.github.filipmalczak.vent.adapter.exception.AmbiguousAdaptationException;
import com.github.filipmalczak.vent.adapter.exception.UnsupportedAdaptationException;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public class Adapters {
    private static ServiceLoader<Adapter> services = ServiceLoader.load(Adapter.class);

    private Adapters(){}

    public static <S, T> T adapt(S source, Class<T> targetClass){
        Adaptation adaptation = Adaptation.between(source.getClass(), targetClass);
        List<Adapter> supporting = StreamSupport.
            stream(services.spliterator(), false).
            filter(a -> a.supports(adaptation).isSupported()).
            sorted().
            limit(2).
            collect(toList());
        if (supporting.isEmpty()) {
            RuntimeException up = new UnsupportedAdaptationException(adaptation);
            throw up;
        }
        if (supporting.size() > 1 && supporting.get(0).supports(adaptation) == supporting.get(1).supports(adaptation)) {
            RuntimeException up = new AmbiguousAdaptationException(adaptation, supporting.get(0).supports(adaptation));
            throw up;
        }
        return supporting.get(0).adapt(source, targetClass);
    }
}
