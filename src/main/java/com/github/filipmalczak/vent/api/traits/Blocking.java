package com.github.filipmalczak.vent.api.traits;

public interface Blocking<ReactiveType extends Reactive<? extends Blocking<ReactiveType>>> {
    ReactiveType asReactive();
}
