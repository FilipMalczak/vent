package com.github.filipmalczak.vent.adapter.impl;

import com.github.filipmalczak.vent.adapter.Adaptation;
import com.github.filipmalczak.vent.adapter.AdaptationSupport;
import com.github.filipmalczak.vent.adapter.Adapter;
import com.github.filipmalczak.vent.adapter.TypeSupport;
import com.github.filipmalczak.vent.traits.paradigm.Blocking;
import com.github.filipmalczak.vent.traits.paradigm.Reactive;
import com.google.auto.service.AutoService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Proxy;

import static com.github.filipmalczak.vent.adapter.TypeSupport.NONE;
import static com.github.filipmalczak.vent.adapter.TypeSupport.TRAIT;

//todo: test me
@AutoService(Adapter.class)
public class GenericBlockingAdapter implements Adapter {
    @Override
    public AdaptationSupport supports(Adaptation adaptation) {
        TypeSupport source = on(Reactive.class, adaptation.getSource()).yield(TRAIT);
        TypeSupport target = on(Blocking.class, adaptation.getSource()).yield(TRAIT);
        return source.with(target);
    }

    @Override
    public <T, S> T adapt(S source, Class<T> targetClass) {
        return adaptWithGenerics(source, targetClass);
    }

    public interface OnClosure {
        TypeSupport yield(TypeSupport toYield);
    }

    protected OnClosure on(Class toCheckForExtending, Class toCheckAgainst){
        return toYield -> Adapter.
            firstExtendsSecond(toCheckForExtending, toCheckAgainst) ?
                toYield :
                NONE;
    }

    protected  <T, S> T adaptWithGenerics(S source, Class<T> targetClass) {
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{targetClass}, (proxy, method, args) -> {
            Object result = method.invoke(source, args);
            if (result instanceof Mono)
                return ((Mono)result).block();
            if (result instanceof Flux)
                return ((Flux)result).toStream();
            return result;
        });
    }


}
