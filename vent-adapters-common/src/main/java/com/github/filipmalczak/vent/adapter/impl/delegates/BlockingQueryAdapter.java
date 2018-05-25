package com.github.filipmalczak.vent.adapter.impl.delegates;

import com.github.filipmalczak.vent.api.blocking.query.BlockingVentQuery;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveVentQuery;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Value
public class BlockingQueryAdapter implements BlockingVentQuery {
    private ReactiveVentQuery query;

    @Override
    public Stream<ObjectSnapshot> find(LocalDateTime queryAt) {
        return query.find(queryAt).toStream();
    }
}
