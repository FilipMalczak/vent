package com.github.filipmalczak.vent.velvet.impl;

import java.util.Map;

public class ByNameSelector extends AbstractSelector{
    private String part;

    public ByNameSelector(Selector parent, Selector child, String part) {
        super(parent, child);
        this.part = part;
    }

    @Override
    public boolean exists(Object target) {
        onlyApplyableTo(Map.class, target);
        return ((Map)target).containsKey(part);
    }

    @Override
    public void set(Object target, Object value) {
        onlyApplyableTo(Map.class, target);
        ((Map)target).put(part, value);
    }

    @Override
    public Object get(Object target) {
        return ((Map)target).get(part);
    }
}
