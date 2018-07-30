package com.github.filipmalczak.vent.api.general.defaults;

import com.github.filipmalczak.vent.api.general.object.VentObjectFacade;
import com.github.filipmalczak.vent.api.general.object.VentObjectReadFacade;
import com.github.filipmalczak.vent.api.general.object.VentObjectWriteFacade;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
public class CompositeFacade<Confirmation, Snapshot> implements VentObjectFacade<Confirmation, Snapshot> {
    @Getter private VentId id;
    @Getter private String collectionName;
    private VentObjectReadFacade<Snapshot> readFacade;
    private VentObjectWriteFacade<Confirmation> writeFacade;


    @Override
    public TemporalService getTemporalService() {
        return readFacade.getTemporalService();
    }

    @Override
    public Snapshot get(LocalDateTime queryAt) {
        return readFacade.get(queryAt);
    }

    @Override
    public Confirmation update(Map newState) {
        return writeFacade.update(newState);
    }

    @Override
    public Confirmation delete() {
        return writeFacade.delete();
    }

    @Override
    public Confirmation putValue(String path, Object value) {
        return writeFacade.putValue(path, value);
    }

    @Override
    public Confirmation deleteValue(String path) {
        return writeFacade.deleteValue(path);
    }
}
