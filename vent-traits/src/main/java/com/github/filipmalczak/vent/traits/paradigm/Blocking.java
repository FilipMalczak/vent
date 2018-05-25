package com.github.filipmalczak.vent.traits.paradigm;

import com.github.filipmalczak.vent.traits.Trait;

/**
 * All methods of implementations return plain objects (in case of single values), Optionals or Streams.
 * Look out! There should be no collections returned!
 */
@Trait
public interface Blocking {
}
