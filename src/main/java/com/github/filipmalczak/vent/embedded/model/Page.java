package com.github.filipmalczak.vent.embedded.model;

import com.github.filipmalczak.vent.api.EventConfirmation;
import com.github.filipmalczak.vent.api.VentId;
import com.github.filipmalczak.vent.embedded.model.events.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static reactor.core.publisher.Mono.empty;
import static reactor.core.publisher.Mono.justOrEmpty;

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

    public Mono<ObjectSnapshot> snapshotAt(@NonNull LocalDateTime localDateTime){
        if (localDateTime.isBefore(startingFrom))
            return empty();
        if (nextPageFrom != null && localDateTime.isAfter(nextPageFrom))
            return empty();
        List<Event> qualifyingEvents = events.stream().
            filter(event ->
                event.getOccuredOn().isBefore(localDateTime) || event.getOccuredOn().isEqual(localDateTime)
            ).collect(Collectors.toList());
        Function<Mono<Map>, Mono<Map>> reducedEvents = qualifyingEvents.stream().
            map(x -> (Function<Mono<Map>, Mono<Map>>) x).
            reduce(Function::andThen).
            orElse(identity());
        return reducedEvents.apply(justOrEmpty(initialState)).
            map(m ->
                ObjectSnapshot.builder().
                    state(m).
                    version(fromVersion+qualifyingEvents.size()).
                    queryTime(localDateTime).
                    lastUpdate(
                        qualifyingEvents.isEmpty() ?
                            startingFrom :
                            qualifyingEvents.get(qualifyingEvents.size()-1).getOccuredOn()
                    ).
                    build()
            );
    }

    public EventConfirmation addEvent(Event event){
        //todo: add validation, e.g. check on this stage whether PutValue will be succesful; same with DeleteValue
        events.add(event);
        return new EventConfirmation(VentId.fromMongoId(objectId), event.getOccuredOn());
    }
}
