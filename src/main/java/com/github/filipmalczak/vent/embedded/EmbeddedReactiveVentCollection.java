package com.github.filipmalczak.vent.embedded;

import com.github.filipmalczak.vent.api.EventConfirmation;
import com.github.filipmalczak.vent.api.ObjectSnapshot;
import com.github.filipmalczak.vent.api.VentId;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.embedded.model.Page;
import com.github.filipmalczak.vent.embedded.model.events.Event;
import com.github.filipmalczak.vent.embedded.model.events.EventFactory;
import com.github.filipmalczak.vent.embedded.service.PageService;
import com.github.filipmalczak.vent.embedded.service.SnapshotService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
public class EmbeddedReactiveVentCollection implements ReactiveVentCollection {
    private @NonNull String collectionName;

    private @NonNull PageService pageService;

    private @NonNull EventFactory eventFactory;

    private @NonNull SnapshotService snapshotService;

    @Override
    public Mono<VentId> create(Map initialState) {
        return pageService.
            createFirstPage(collectionName, initialState).
            map(Page::getObjectId).
            map(VentId::fromMongoId);
    }

    @Override
    public Mono<EventConfirmation> putValue(VentId id, String path, Object value){
        return addEvent(id, eventFactory.putValue(path, value));
    }

    @Override
    public Mono<EventConfirmation> deleteValue(VentId id, String path) {
        return addEvent(id, eventFactory.deleteValue(path));
    }


    @Override
    public Mono<ObjectSnapshot> get(VentId id, LocalDateTime queryAt) {
        return snapshotService.getSnapshot(collectionName, id, queryAt);
    }

    @Override
    public Mono<EventConfirmation> update(VentId id, Map newState) {
        //todo right after adding UPDATE event, new page should be created (with snapshot from right after UPDATE)
        return addEvent(id, eventFactory.update(newState));
    }

    private Mono<EventConfirmation> addEvent(VentId id, Event event){
        return pageService.
            currentPage(collectionName, id).
            flatMap(p -> pageService.addEvent(collectionName, p, event));
    }
}
