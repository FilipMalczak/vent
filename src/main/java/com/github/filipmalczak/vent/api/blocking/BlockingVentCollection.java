package com.github.filipmalczak.vent.api.blocking;

import com.github.filipmalczak.vent.api.EventConfirmation;
import com.github.filipmalczak.vent.api.ObjectSnapshot;
import com.github.filipmalczak.vent.api.VentId;

import java.time.LocalDateTime;
import java.util.Map;

import static com.github.filipmalczak.vent.helper.Struct.map;

public interface BlockingVentCollection {
    VentId create(Map initialState);

    default VentId create(){
        return create(map());
    }

    EventConfirmation putValue(VentId id, String path, Object value);

    EventConfirmation deleteValue(VentId id, String path);

    ObjectSnapshot get(VentId id, LocalDateTime queryAt);

    EventConfirmation update(VentId id, Map newState);
}
