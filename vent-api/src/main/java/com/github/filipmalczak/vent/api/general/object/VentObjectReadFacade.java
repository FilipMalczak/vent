package com.github.filipmalczak.vent.api.general.object;

import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.temporal.TemporallyEnabled;

import java.time.LocalDateTime;

public interface VentObjectReadFacade<Snapshot> extends TemporallyEnabled {
    VentId getId();
    String getCollectionName();

    Snapshot get(LocalDateTime queryAt);

    default Snapshot get(){
        return get(getTemporalService().now());
    }
}
