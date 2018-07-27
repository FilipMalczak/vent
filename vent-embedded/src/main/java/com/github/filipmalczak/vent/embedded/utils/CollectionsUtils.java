package com.github.filipmalczak.vent.embedded.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionsUtils {
    public static final String COLLECTIONS_MONGO_COLLECTION = "vent.collections";

    public static MongoCollectionNameMapper MONGO_COLLECTION_NAME_MAPPER = (c, f, t) ->
        "vent."+
            c+
            "__"+
            f.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)+
            t.
                map(
                    tv ->
                        "__"+
                            tv.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                ).orElse(
                ""
            );


    @FunctionalInterface
    public interface MongoCollectionNameMapper {
        String toMongoCollectionName(String ventCollectionName, LocalDateTime from, Optional<LocalDateTime> to);
    }
}
