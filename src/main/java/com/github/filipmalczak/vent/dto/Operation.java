package com.github.filipmalczak.vent.dto;

public enum Operation {
    /**
     * Create the object with optional initial value - must be the first vent ever (including memories, not only current
     * object events).
     */
    CREATE,
    /**
     * Delete object. Once this vent appears, no other vent will follow and the object (when queried in time after this
     * vent) is considered non-existent.
     */
    DELETE,
    /**
     * Set value under some path in the boject to specified one.
     */
    SET,
    /**
     * Add specified value to list under some path. Path must be pointing to a list or a vent won't be added to objects
     * events.
     */
    ADD,
    /**
     * Remove field or index pointed by the path from the object.
     */
    REMOVE,
    /**
     * Replace previoius state of the object with specified one. In other words, override the whole object.
     */
    PUT,
    //todo: update? merge?
    /**
     * Vent the whole object and start gathering new vents, saving old ones to historical data. Does not introduce any
     * data changes, but is useful for auditing.
     *
     * Compacting might occur on other occasions too, and they will not be saved as an operation (e.g. on vent crowding
     * or after PUT, for the sake of optimization). Vents with this operation will be stored only as a result of
     * explicit client command.
     */
    COMPACT
}
