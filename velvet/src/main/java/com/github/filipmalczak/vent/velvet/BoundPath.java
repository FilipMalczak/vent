package com.github.filipmalczak.vent.velvet;

public interface BoundPath {
    String getPath();

    Object getTarget();

    boolean exists();

    void set(Object value);

    Object get();

    void delete();
}
