package com.github.filipmalczak.vent.traits;

public interface Reactive<BlockingType extends Blocking<? extends Reactive<BlockingType>>> {
    BlockingType asBlocking();
}
