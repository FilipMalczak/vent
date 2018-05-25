package com.github.filipmalczak.vent.api.general;

import lombok.NonNull;

public interface VentDb<CollectionImpl extends VentCollection<SingleSuccess, ?, ?, ?, ?, ?, ?>, ManyStrings, SingleSuccess, SingleBoolean> {
    //todo this may somehow become optional
    //todo maybe these should return SingleSuccess and SingleBoolean?
    /**
     * Should be no-op is isInitialized returns true.
     */
    default void initialize(){}
    default boolean isInitialized(){return true;}

    CollectionImpl getCollection(String collectionName);

    default SingleSuccess drop(String collectionName){
        return getCollection(collectionName).drop();
    }

    //todo: figure out some fluent API
    //todo this will probably evolve; not sure whether this is required at all
    SingleSuccess optimizePages(SuggestionStrength strength, OptimizationType type);

    //todo these enums will probably be moved up

    enum SuggestionStrength {
        /**
         * If that won't bother you too much, do the optimization now. I don't really care, but it was some time already
         * since I've asked, so it won't hurt.
         */
        CASUAL,
        /**
         * Unless you are Busy (with capital B), do the optimization now. World won't end if you won't, but do not
         * ignore this request too often.
         */
        STRONG,
        /**
         * This is not a request, you MUST do the optimization now.
         */
        REQUIRED;
    }

    //todo these will probably grow; may even stop being an enum and become some "optimization target criteria" object
    enum OptimizationType {
        /**
         * Optimize each and every page with non-empty event list.
         */
        FULL,
        /**
         * Optimize those pages (with non-empty event lists) that fit some heuristic criteria.
         */
        PARTIAL;
    }

    //todo these will possibly move to some kind of exposed ventDbDescriptor concept; it may be related to embedded model class, but doesnt have to
    ManyStrings getManagedCollections();
    SingleBoolean isManaged(@NonNull String collectionName);
    SingleSuccess manage(String collectionName);
}
