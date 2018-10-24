package com.github.filipmalczak.vent.mongo.extension.scheduling;

import java.time.Duration;

/**
 * Mixin-style interface for utility methods
 * @param <T> may be schedule scheme, but also task closure, etc
 */
//package-private intentionally
interface WithEvery<T> {
    T every(Duration duration);

    default T every5s(){
        return every(Duration.ofSeconds(5));
    }

    default T every15s(){
        return every(Duration.ofSeconds(15));
    }

    default T hourly(){
        return every(Duration.ofHours(1));
    }

    default T daily(){
        return every(Duration.ofDays(1));
    }
}
