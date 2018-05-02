package com.github.filipmalczak.vent.embedded.service;

import com.github.filipmalczak.vent.api.VentId;
import com.github.filipmalczak.vent.embedded.model.ObjectSnapshot;
import com.github.filipmalczak.vent.embedded.model.Page;
import com.github.filipmalczak.vent.embedded.model.events.Event;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class SnapshotService {
    @Autowired
    private SnapshotRenderer snapshotRenderer;

    @Autowired
    private PageService pageService;

    @Autowired
    private TemporalService temporalService;

    public Mono<ObjectSnapshot> getSnapshot(@NonNull String collectionName, @NonNull VentId ventId){
        return getSnapshot(collectionName, ventId, temporalService.now());
    }

    public Mono<ObjectSnapshot> getSnapshot(@NonNull String collectionName, @NonNull VentId ventId, @NonNull LocalDateTime queryAt){
        return pageService.
            pageAtTimestamp(collectionName, ventId, queryAt).
            map(p ->
                render(
                    p.getFromVersion(),
                    p.getStartingFrom(),
                    p.getInstructionsForSnapshotAt(queryAt),
                    queryAt
                )
            );
    }

    private ObjectSnapshot render(long pageFromVersion, LocalDateTime pageFrom, Page.SnapshotInstructions instructions, LocalDateTime queryAt){
        Map object = snapshotRenderer.render(instructions);
        List<Event> events = instructions.getEvents();
        return ObjectSnapshot.builder().
            state(object).
            version(pageFromVersion + events.size()).
            queryTime(queryAt).
            lastUpdate(events.isEmpty() ? pageFrom : events.get(events.size()-1).getOccuredOn()).
            build();
    }
}
