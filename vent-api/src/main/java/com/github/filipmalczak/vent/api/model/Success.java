package com.github.filipmalczak.vent.api.model;



/**
 * Similiar to com.mongodb.reactivestreams.client.Success, to isolate Vent from MongoDB API.
 */
public enum Success {
    /**
     * Operation was performed and finished successfully.
     * Example usage: result of createIfAbsent(...) in "if absent" case.
     */
    SUCCESS,
    /**
     * No operation was performed, but this should still be treated as success.
     * Example usage: result of createIfAbsent(...) in "present" case.
     */
    NO_OP_SUCCESS;
}
