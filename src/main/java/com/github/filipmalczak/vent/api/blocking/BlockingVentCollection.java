package com.github.filipmalczak.vent.api.blocking;

import com.github.filipmalczak.vent.api.EventConfirmation;
import com.github.filipmalczak.vent.api.ObjectSnapshot;
import com.github.filipmalczak.vent.api.Success;
import com.github.filipmalczak.vent.api.VentId;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.api.traits.Blocking;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.filipmalczak.vent.helper.Struct.map;

public interface BlockingVentCollection extends Blocking<ReactiveVentCollection> {
    Success drop();

    VentId create(Map initialState);

    default VentId create(){
        return create(map());
    }

    EventConfirmation putValue(VentId id, String path, Object value);

    EventConfirmation deleteValue(VentId id, String path);

    ObjectSnapshot get(VentId id, LocalDateTime queryAt);

    Stream<VentId> identifyAll(LocalDateTime queryAt);

    default Stream<ObjectSnapshot> getAll(LocalDateTime queryAt){
        return identifyAll(queryAt).map(id -> get(id, queryAt));
    }

    EventConfirmation update(VentId id, Map newState);
}
