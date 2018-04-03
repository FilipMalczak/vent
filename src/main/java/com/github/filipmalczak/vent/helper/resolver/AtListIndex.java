package com.github.filipmalczak.vent.helper.resolver;

import lombok.Value;

import java.util.List;

@Value(staticConstructor = "from")
class AtListIndex implements ResolvedPath {
    private List parent;
    private int idx;

    @Override
    public boolean exists() {
        return idx >= 0 && idx < parent.size();
    }

    @Override
    public Object get() {
        return parent.get(idx);
    }

    @Override
    public void set(Object o) {
        parent.set(idx, o);
    }

    @Override
    public void delete() {
        parent.remove(idx);
    }
}
