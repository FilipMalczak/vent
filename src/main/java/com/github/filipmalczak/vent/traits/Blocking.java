package com.github.filipmalczak.vent.traits;

public interface Blocking<ReactiveType extends Reactive<? extends Blocking<ReactiveType>>> {
    ReactiveType asReactive();
}
