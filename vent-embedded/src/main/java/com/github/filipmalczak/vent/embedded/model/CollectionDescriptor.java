package com.github.filipmalczak.vent.embedded.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.github.filipmalczak.vent.embedded.utils.CollectionsUtils.COLLECTIONS_MONGO_COLLECTION;
import static com.github.filipmalczak.vent.embedded.utils.CollectionsUtils.MONGO_COLLECTION_NAME_MAPPER;

@Document
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class CollectionDescriptor {
    @Id
    private ObjectId ventCollectionId;
    @NonNull private String ventCollectionName;
    @NonNull private CollectionPeriodDescriptor currentPeriod;
    @NonNull private List<CollectionPeriodDescriptor> previousPeriods;

    public CollectionDescriptor asFinishedOn(LocalDateTime end){
        List<CollectionPeriodDescriptor> newPeriods = new ArrayList<>(previousPeriods);
        newPeriods.add(currentPeriod.asFinishedOn(ventCollectionName, end));
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
}
