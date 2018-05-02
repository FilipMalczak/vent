package com.github.filipmalczak.vent.embedded.service;

import com.github.filipmalczak.vent.embedded.model.Page;
import com.github.filipmalczak.vent.embedded.model.events.Event;

import java.util.List;
import java.util.Map;

public interface SnapshotRenderer {

    Map render(Map initialSnapshot, List<Event> events);

    default Map render(Page.SnapshotInstructions instructions){
        return render(instructions.getInitialSnapshot(), instructions.getEvents());
    }
}
