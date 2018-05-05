package com.github.filipmalczak.vent.embedded.model;

import com.github.filipmalczak.vent.api.EventConfirmation;
import com.github.filipmalczak.vent.api.VentId;
import com.github.filipmalczak.vent.embedded.model.events.Event;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Document
@Data
@AllArgsConstructor
public class Page {
    @Id
    private ObjectId pageId;
    private ObjectId objectId;
    private ObjectId previousPageId;
    private ObjectId nextPageId;
    private long fromVersion;
    private LocalDateTime startingFrom;
    private LocalDateTime nextPageFrom;
    private Map initialState;
    private List<Event> events;

    public boolean describesStateAt(LocalDateTime at){
        return (at.isAfter(startingFrom) || at.isEqual(startingFrom)) && (nextPageFrom == null || nextPageFrom.isAfter(at));
    }

    /**
     * Returns stream of events in chronological order, ending with last event that happens before argument or exactly
     * at that moment, or empty stream if queried timestamp is outside of this page.
     */
    //todo: should this be public?
    public Stream<Event> getEventsForSnapshotAt(@NonNull LocalDateTime snapshotAt){
        if (!describesStateAt(snapshotAt))
            return Stream.empty();
        return events.stream().filter(event ->
            event.getOccuredOn().isBefore(snapshotAt) || event.getOccuredOn().isEqual(snapshotAt)
        );
    }

    public Optional<SnapshotInstructions> getInstructionsForSnapshotAt(@NonNull LocalDateTime snapshotAt){
        if (!describesStateAt(snapshotAt))
            return Optional.empty();
        //todo deep copy of initialState
        //if persistence would cache retrieved pages and we'd pass initialState for rendering, its object tree
        //would probably change, so cached page would have different state than persisted one
        return Optional.of(
            new SnapshotInstructions(
                initialState,
                getEventsForSnapshotAt(snapshotAt).
                    collect(toList())
            )
        );
    }

    public EventConfirmation addEvent(Event event){
        //todo: add validation, e.g. check on this stage whether PutValue will be succesful; same with DeleteValue
        events.add(event);
        return new EventConfirmation(VentId.fromMongoId(objectId), event.getOccuredOn());
    }

    /**
     * Simple one-shot DTO for transferring needed data between Page and SnapshotRenderer.
     */
    @Value
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SnapshotInstructions {
        private final Map initialSnapshot;
        private final List<Event> events;
    }
}
