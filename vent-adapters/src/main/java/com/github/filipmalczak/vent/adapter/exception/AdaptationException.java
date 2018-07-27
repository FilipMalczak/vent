package com.github.filipmalczak.vent.adapter.exception;

public class AdaptationException extends RuntimeException {
    public AdaptationException() {
    }

    public AdaptationException(String message) {
        super(message);
    }

    public AdaptationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdaptationException(Throwable cause) {
        super(cause);
    }

    public AdaptationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
