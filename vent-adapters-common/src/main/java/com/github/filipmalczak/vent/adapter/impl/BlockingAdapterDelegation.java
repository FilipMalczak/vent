package com.github.filipmalczak.vent.adapter.impl;

import com.github.filipmalczak.vent.adapter.Adaptation;
import com.github.filipmalczak.vent.adapter.AdaptationSupport;
import com.github.filipmalczak.vent.adapter.Adapter;
import com.github.filipmalczak.vent.adapter.TypeSupport;
import com.github.filipmalczak.vent.adapter.impl.delegates.BlockingCollectionAdapter;
import com.github.filipmalczak.vent.adapter.impl.delegates.BlockingDbAdapter;
import com.github.filipmalczak.vent.adapter.impl.delegates.BlockingQueryAdapter;
import com.github.filipmalczak.vent.adapter.impl.delegates.BlockingQueryBuilderAdapter;
import com.github.filipmalczak.vent.api.blocking.BlockingVentDb;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveQueryBuilder;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveVentQuery;
import com.github.filipmalczak.vent.traits.paradigm.Blocking;
import com.google.auto.service.AutoService;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.Optional;

import static com.github.filipmalczak.vent.helper.Struct.map;
import static com.github.filipmalczak.vent.helper.Struct.pair;

//todo this can probably be used as basis for AbstractDelegatingAdapter
@AutoService(Adapter.class)
public class BlockingAdapterDelegation implements Adapter {
    private static final Map<Class, Class> ADAPTER_IMPL = map(
        pair(ReactiveVentDb.class, BlockingDbAdapter.class),
        pair(ReactiveVentCollection.class, BlockingCollectionAdapter.class),
        pair(ReactiveQueryBuilder.class, BlockingQueryBuilderAdapter.class),
        pair(ReactiveVentQuery.class, BlockingQueryAdapter.class)
    );

    private static final Map<Class, Class> ADAPTER_INTERFACES = map(
        pair(ReactiveVentDb.class, BlockingVentDb.class),
        pair(ReactiveVentCollection.class, BlockingCollectionAdapter.class),
        pair(ReactiveQueryBuilder.class, BlockingQueryBuilderAdapter.class),
        pair(ReactiveVentQuery.class, BlockingQueryAdapter.class)
    );

    @Override
    public AdaptationSupport supports(Adaptation adaptation) {
        TypeSupport sourceSupport = findSourceSupport(adaptation.getSource());
        if (ADAPTER_IMPL.containsValue(adaptation.getTarget()))
            return sourceSupport.with(TypeSupport.IMPLEMENTATION);
        if (ADAPTER_INTERFACES.containsValue(adaptation.getTarget()))
            return sourceSupport.with(TypeSupport.INTERFACE);
        if (adaptation.getTarget().isAssignableFrom(Blocking.class))
            return sourceSupport.with(TypeSupport.TRAIT);
        return sourceSupport.with(TypeSupport.NONE);
    }

    private TypeSupport findSourceSupport(Class source) {
        return findMatchingSource(source).map(i -> TypeSupport.INTERFACE).orElse(TypeSupport.NONE);
    }

    private Optional<Class> findMatchingSource(Class source){
        return ADAPTER_IMPL.keySet().stream().filter(i -> i.isAssignableFrom(source)).findAny();
    }

    @Override
    @SneakyThrows
    public <T, S> T adapt(S source, Class<T> targetClass) {
        Class sourceClass = source.getClass();
        // by contract adapt() will be called only when AdaptationSupport from supports() has
        // no NONE for source nor target, so this is safe
        Class sourceKey = findMatchingSource(sourceClass).get();
        return (T) ADAPTER_IMPL.get(sourceKey).getConstructor(sourceKey).newInstance(source);
    }
}
