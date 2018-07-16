package com.github.filipmalczak.vent.web.integration;

import com.github.filipmalczak.vent.api.model.EventConfirmation;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.web.model.EventConfirmationView;
import com.github.filipmalczak.vent.web.model.IdView;
import com.github.filipmalczak.vent.web.model.ObjectView;

public class Converters {
    public IdView convert(VentId ventId){
        return new IdView(
            ventId.getValue()
        );
    }

    public VentId convert(IdView idView){
        return new VentId(
            idView.getId()
        );
    }

    public ObjectView convert(ObjectSnapshot objectSnapshot){
        return new ObjectView(
            convert(objectSnapshot.getVentId()),
            objectSnapshot.getState(),
            objectSnapshot.getVersion(),
            objectSnapshot.getQueryTime(),
            objectSnapshot.getLastUpdate()
        );
    }

    public ObjectSnapshot convert(ObjectView objectView){
        return new ObjectSnapshot(
            convert(objectView.getVentId()),
            objectView.getView(),
            objectView.getVersion(),
            objectView.getQueryTime(),
            objectView.getLastUpdate()
        );
    }

    public EventConfirmationView convert(EventConfirmation eventConfirmation){
        return new EventConfirmationView(
            convert(eventConfirmation.getVentId()),
            eventConfirmation.getHappenedOn()
        );
    }

    public EventConfirmation convert(EventConfirmationView eventConfirmationView){
        return new EventConfirmation(
            convert(eventConfirmationView.getId()),
            eventConfirmationView.getHappenedOn()
        );
    }
}
