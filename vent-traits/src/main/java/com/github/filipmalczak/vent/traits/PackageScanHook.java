package com.github.filipmalczak.vent.traits;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Documentational annotation to specify empty top-package interfaces in a module, created only to be easily
 * referenced when configuring Spring or other package-scan exploiting tool.
 */
//todo maybe all traits should be annotation-based?
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface PackageScanHook {
}
