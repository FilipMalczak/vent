package com.github.filipmalczak.vent.traits.adapters;

import com.github.filipmalczak.vent.api.blocking.BlockingVentCollection;
import com.github.filipmalczak.vent.api.blocking.BlockingVentDb;
import com.github.filipmalczak.vent.api.blocking.BlockingVentQuery;
import com.github.filipmalczak.vent.api.query.BlockingQueryBuilder;
import com.github.filipmalczak.vent.api.query.ReactiveQueryBuilder;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentQuery;

public class Adapters {
    private Adapters(){}

    public static BlockingVentDb adapt(ReactiveVentDb ventDb){
        return new BlockingDbAdapter(ventDb);
    }

    public static BlockingVentCollection adapt(ReactiveVentCollection ventCollection){
        return new BlockingCollectionAdapter(ventCollection);
    }

    public static BlockingVentQuery adapt(ReactiveVentQuery ventQuery){
        return new BlockingQueryAdapter(ventQuery);
    }

    public static BlockingQueryBuilder<BlockingQueryBuilderAdapter, BlockingVentQuery> adapt(ReactiveQueryBuilder<?, ?> builder){
        return new BlockingQueryBuilderAdapter(builder);
    }
}
