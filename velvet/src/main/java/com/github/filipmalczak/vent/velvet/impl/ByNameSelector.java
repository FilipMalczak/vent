package com.github.filipmalczak.vent.velvet.impl;

import java.util.Map;

public class ByNameSelector extends AbstractSelector<Map>{
    private String part;

    public ByNameSelector(Selector parent, Selector child, String part) {
        super(parent, child);
        this.part = part;
    }

    @Override
    public String getUnparsedSelector() {
        return "."+part;
    }

    @Override
    protected Class<Map> applyableTo() {
        return Map.class;
    }

    @Override
    protected boolean existsImpl(Map target) {
        return target.containsKey(part);
    }

    @Override
    protected void setImpl(Map target, Object value) {
        target.put(part, value);
    }

    @Override
    protected Object getImpl(Map target) {
        return target.get(part);
    }

    @Override
    protected void deleteImpl(Map target) {
        target.remove(part);
    }
}
