package com.github.filipmalczak.vent.api.general;

import com.github.filipmalczak.vent.api.general.defaults.ObjectWriteFacade;
import com.github.filipmalczak.vent.api.general.object.VentObjectWriteFacade;
import com.github.filipmalczak.vent.api.model.VentId;

import java.util.HashMap;
import java.util.Map;

public interface VentCollectionWriteOperations<SingleSuccess, SingleId, SingleConfirmation>{
    SingleSuccess drop();

    SingleId create(Map initialState);

    default SingleId create(){
        return create(new HashMap());
    }

    SingleConfirmation update(VentId id, Map newState);
    SingleConfirmation delete(VentId id);

    SingleConfirmation putValue(VentId id, String path, Object value);
    SingleConfirmation deleteValue(VentId id, String path);

    default VentObjectWriteFacade<SingleConfirmation> getWriteFacade(VentId id){
        return new ObjectWriteFacade<>(id, this);
    }
}
