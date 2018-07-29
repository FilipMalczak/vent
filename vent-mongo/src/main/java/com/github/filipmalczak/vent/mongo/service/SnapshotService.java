package com.github.filipmalczak.vent.mongo.service;

import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.mongo.model.Page;
import com.github.filipmalczak.vent.mongo.model.events.Event;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.github.filipmalczak.vent.mongo.utils.MongoTranslator.fromMongo;


@AllArgsConstructor
public class SnapshotService {
    private SnapshotRenderer snapshotRenderer;
    private PageService pageService;

    public Mono<ObjectSnapshot> getSnapshot(@NonNull String collectionName, @NonNull VentId ventId, @NonNull LocalDateTime queryAt){
        return pageService.
            pageAtTimestamp(collectionName, ventId, queryAt).
            map(p -> render(p, queryAt));
    }

    public ObjectSnapshot render(Page page, LocalDateTime queryAt){
        return render(
            fromMongo(page.getObjectId()),
            page.getFromVersion(),
            page.getStartingFrom(),
            //todo
            page.
                getInstructionsForSnapshotAt(queryAt).
                orElseThrow(() -> new RuntimeException("This page in not renderable at that timestamp!")),
            queryAt
        );
    }

    private ObjectSnapshot render(VentId ventId, long pageFromVersion, LocalDateTime pageFrom, Page.SnapshotInstructions instructions, LocalDateTime queryAt){
        Map object = snapshotRenderer.render(instructions);
        List<Event> events = instructions.getEvents();
        return ObjectSnapshot.builder().
            ventId(ventId).
            state(object).
            version(pageFromVersion + events.size()).
            queryTime(queryAt).
            lastUpdate(events.isEmpty() ? pageFrom : events.get(events.size()-1).getOccuredOn()).
            build();
    }
}
