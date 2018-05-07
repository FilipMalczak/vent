package com.github.filipmalczak.vent.api;

/**
 * Equivalent of com.mongodb.reactivestreams.client.Success, to isolate Vent from MongoDB API.
 * fixme VentId and this still expose mongo-related results from methods, so isolation is broken
 */
public enum Success {
    SUCCESS;

    public com.mongodb.reactivestreams.client.Success toMongoSuccess(){
        return com.mongodb.reactivestreams.client.Success.SUCCESS;
    }

    public static Success fromMongoSuccess(com.mongodb.reactivestreams.client.Success success){
        return SUCCESS;
    }
}
