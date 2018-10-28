package com.github.filipmalczak.vent.mongo.model;

import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

import static com.github.filipmalczak.vent.mongo.utils.CollectionsUtils.MONGO_COLLECTION_NAME_MAPPER;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class CollectionPeriodDescriptor {
    @NonNull
    private LocalDateTime from;
    private LocalDateTime to;
    /**
     * MongoDB collection name. Many MongoDB collections map to single Vent collection.
     */
    @NonNull private String mongoCollectionName;

    //no isCurrent to avoid serializing this "property"
    boolean current() {
        return to == null;
    }

    public Optional<Duration> duration(){
        return Optional.ofNullable(to).map(t -> Duration.between(from, t));
    }

    public Duration duration(Supplier<LocalDateTime> now){
        return duration().orElseGet(() -> Duration.between(from, now.get()));
    }

    public CollectionPeriodDescriptor asFinishedOn(String ventCollectionName, LocalDateTime end){
        if (!current())
            throw new IllegalStateException("Only current period descriptor can be used to produce a finished one");
        return new CollectionPeriodDescriptor(
            from,
            end,
            MONGO_COLLECTION_NAME_MAPPER.
                toMongoCollectionName(ventCollectionName, from, Optional.of(end))
        );
    }
}
