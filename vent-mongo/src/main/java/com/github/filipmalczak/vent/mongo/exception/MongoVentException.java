package com.github.filipmalczak.vent.mongo.exception;

public class MongoVentException extends RuntimeException{
    public MongoVentException() {
    }

    public MongoVentException(String message) {
        super(message);
    }

    public MongoVentException(String message, Throwable cause) {
        super(message, cause);
    }

    public MongoVentException(Throwable cause) {
        super(cause);
    }

    public MongoVentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
