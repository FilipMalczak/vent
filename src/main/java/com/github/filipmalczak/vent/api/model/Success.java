package com.github.filipmalczak.vent.api.model;



/**
 * Equivalent of com.mongodb.reactivestreams.client.Success, to isolate Vent from MongoDB API.
 * fixme VentId and this still expose mongo-related results from methods, so isolation is broken
 */

public enum Success {
    SUCCESS;
}
