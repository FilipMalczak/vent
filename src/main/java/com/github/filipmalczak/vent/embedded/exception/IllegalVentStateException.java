package com.github.filipmalczak.vent.embedded.exception;

public class IllegalVentStateException extends EmbeddedVentException {
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
