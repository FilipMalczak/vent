package com.github.filipmalczak.vent.mongo.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.filipmalczak.vent.mongo.utils.CollectionsUtils.MONGO_COLLECTION_NAME_MAPPER;

@Document
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class CollectionDescriptor {
    @Id
    private ObjectId ventCollectionId;
    /**
     * Collection name, as used when working with Vent. Single Vent collection is mapped to one or more MongoDB
     * collections, each representing a time period.
     */
    @NonNull private String ventCollectionName;
    @NonNull private CollectionPeriodDescriptor currentPeriod;
    @NonNull private List<CollectionPeriodDescriptor> previousPeriods;

    public CollectionDescriptor asArchivedOn(LocalDateTime end){
        List<CollectionPeriodDescriptor> newPeriods = new ArrayList<>();
        newPeriods.add(currentPeriod.asFinishedOn(ventCollectionName, end));
        newPeriods.addAll(previousPeriods);
        return new CollectionDescriptor(
            ventCollectionName,
            new CollectionPeriodDescriptor(
                end,
                null,
                MONGO_COLLECTION_NAME_MAPPER.
                    toMongoCollectionName(ventCollectionName, end, Optional.empty())
            ),
            newPeriods
        );
    }

    public Stream<CollectionPeriodDescriptor> getAllPeriods(){
        return Stream.concat(Stream.of(currentPeriod), previousPeriods.stream());
    }

    public LocalDateTime getOldestFrom(){
        return getAllPeriods().map(CollectionPeriodDescriptor::getFrom).min(Comparator.comparing(Function.identity())).get();
    }

    public Optional<LocalDateTime> getLatestTo(){
        return getAllPeriods().
            filter(((Predicate<CollectionPeriodDescriptor>)CollectionPeriodDescriptor::current).negate()).
            map(CollectionPeriodDescriptor::getTo).
            min(Comparator.comparing(Function.<LocalDateTime>identity()).reversed());
    }

    public Duration duration(Supplier<LocalDateTime> now){
        return Duration.between(getOldestFrom(), now.get());
    }
}
