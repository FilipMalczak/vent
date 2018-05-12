package com.github.filipmalczak.vent.traits.adapters;

import com.github.filipmalczak.vent.api.ObjectSnapshot;
import com.github.filipmalczak.vent.api.blocking.BlockingVentQuery;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentQuery;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Value
class BlockingQueryAdapter implements BlockingVentQuery {
    private ReactiveVentQuery query;

    @Override
    public Stream<ObjectSnapshot> find(LocalDateTime queryAt) {
        return query.find(queryAt).toStream();
    }

    @Override
    public ReactiveVentQuery asReactive() {
        return query;
    }
}
