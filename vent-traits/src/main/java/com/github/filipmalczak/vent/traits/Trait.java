package com.github.filipmalczak.vent.traits;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicator that a no-method interface is a marker describing the way that implementation is using to
 * contact the outside world - for example to state that implementation is blocking or asynchronous.
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Trait {
}
