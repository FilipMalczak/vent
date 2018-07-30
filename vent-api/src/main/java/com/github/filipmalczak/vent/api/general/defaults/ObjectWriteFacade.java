package com.github.filipmalczak.vent.api.general.defaults;

import com.github.filipmalczak.vent.api.general.VentCollectionWriteOperations;
import com.github.filipmalczak.vent.api.general.object.VentObjectWriteFacade;
import com.github.filipmalczak.vent.api.model.VentId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
public class ObjectWriteFacade<Confirmation> implements VentObjectWriteFacade<Confirmation> {
    @Getter private VentId id;
    private VentCollectionWriteOperations<?, ?, Confirmation> writeOperations;

    @Override
    public Confirmation update(Map newState) {
        return writeOperations.update(id, newState);
    }

    @Override
    public Confirmation delete() {
        return writeOperations.delete(id);
    }

    @Override
    public Confirmation putValue(String path, Object value) {
        return writeOperations.putValue(id, path, value);
    }

    @Override
    public Confirmation deleteValue(String path) {
        return writeOperations.deleteValue(id, path);
    }
}
