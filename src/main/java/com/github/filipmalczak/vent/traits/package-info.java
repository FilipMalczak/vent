//todo make Blocking, Reactive marker interfaces; delete asBlocking/asReactive, use adapters for that
//todo move adapters up a level, so they can be moved to another module
//todo introduce ServiceLoader-based adapter with API like adapt(reactiveVentDb).to(Blocking.class)
//todo introduce Asynchronous trait (use Streams in place of Fluxes) - extend API to adapt(someDb).to(Asynchronous.class).with(AsyncConfig.withExecutor(...)) or alike
package com.github.filipmalczak.vent.traits;