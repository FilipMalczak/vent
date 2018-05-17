package com.github.filipmalczak.vent.embedded.exception;

public class EmbeddedVentException extends RuntimeException{
    public EmbeddedVentException() {
    }

    public EmbeddedVentException(String message) {
        super(message);
    }

    public EmbeddedVentException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmbeddedVentException(Throwable cause) {
        super(cause);
    }

    public EmbeddedVentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
