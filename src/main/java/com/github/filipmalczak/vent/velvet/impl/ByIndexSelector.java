package com.github.filipmalczak.vent.velvet.impl;


import java.util.List;

public class ByIndexSelector extends AbstractSelector{
    private int index;

    public ByIndexSelector(Selector parent, Selector child, int index) {
        super(parent, child);
        this.index = index;
    }

    @Override
    public boolean exists(Object target) {
        onlyApplyableTo(List.class, target);
        return ((List)target).size() > index;
    }

    @Override
    public void set(Object target, Object value) {
        onlyApplyableTo(List.class, target);
        ((List)target).set(index, value);
    }

    @Override
    public Object get(Object target) {
        onlyApplyableTo(List.class, target);
        return ((List)target).get(index);
    }
}
