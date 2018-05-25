package com.github.filipmalczak.vent.adapter;

import lombok.Value;

@Value(staticConstructor = "between")
public class Adaptation {
    private Class source;
    private Class target;
}
