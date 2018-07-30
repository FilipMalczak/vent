package com.github.filipmalczak.vent.api.general.defaults;

import com.github.filipmalczak.vent.api.general.VentCollectionReadOperations;
import com.github.filipmalczak.vent.api.general.object.VentObjectReadFacade;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ObjectReadFacade<Snapshot> implements VentObjectReadFacade<Snapshot> {
    @Getter private VentId id;
    @Getter private String collectionName;
    private VentCollectionReadOperations<Snapshot, ?, ?, ?> readOperations;

    @Override
    public Snapshot get(LocalDateTime queryAt) {
        return readOperations.get(id, queryAt);
    }

    @Override
    public TemporalService getTemporalService() {
        return readOperations.getTemporalService();
    }
}
