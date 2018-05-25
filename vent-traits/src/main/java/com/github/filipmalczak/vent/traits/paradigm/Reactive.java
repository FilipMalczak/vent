package com.github.filipmalczak.vent.traits.paradigm;

import com.github.filipmalczak.vent.traits.Trait;

/**
 * Reactive implementations should have methods returning Mono or Flux - or other reactive implementations
 * Remember that in case of commands/procedures you should return {@code Mono<?>} or {@code Mono<Void>} instead of void!
 */
//fixme this looks like "general reactive", while checks for "reactor reactive"
@Trait
public interface Reactive {
}
