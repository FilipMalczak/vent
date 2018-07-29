package com.github.filipmalczak.vent.mongo.exception;

public class IllegalVentStateException extends MongoVentException {
    public IllegalVentStateException() {
    }

    public IllegalVentStateException(String message) {
        super(message);
    }

    public IllegalVentStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalVentStateException(Throwable cause) {
        super(cause);
    }

    public IllegalVentStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
