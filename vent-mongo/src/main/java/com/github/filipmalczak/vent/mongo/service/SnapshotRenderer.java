package com.github.filipmalczak.vent.mongo.service;

import com.github.filipmalczak.vent.mongo.model.Page;
import com.github.filipmalczak.vent.mongo.model.events.Event;

import java.util.List;
import java.util.Map;


public interface SnapshotRenderer {

    Map render(Map initialSnapshot, List<Event> events);

    default Map render(Page.SnapshotInstructions instructions){
        return render(instructions.getInitialSnapshot(), instructions.getEvents());
    }
}
