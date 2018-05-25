package com.github.filipmalczak.vent.adapter;

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
        if (supporting.isEmpty())
            throw new RuntimeException(); //todo
        if (supporting.size() > 1 && supporting.get(0).supports(adaptation) == supporting.get(1).supports(adaptation))
            throw new RuntimeException(); //todo
        return supporting.get(0).addapt(source, targetClass);
    }
}
