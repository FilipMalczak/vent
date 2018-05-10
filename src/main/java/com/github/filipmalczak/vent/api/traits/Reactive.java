package com.github.filipmalczak.vent.api.traits;

public interface Reactive<BlockingType extends Blocking<? extends Reactive<BlockingType>>> {
    BlockingType asBlocking();
}
