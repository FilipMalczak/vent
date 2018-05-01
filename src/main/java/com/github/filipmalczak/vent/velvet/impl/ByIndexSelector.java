package com.github.filipmalczak.vent.velvet.impl;


import java.util.List;

public class ByIndexSelector extends AbstractSelector<List>{
    private int index;

    public ByIndexSelector(Selector parent, Selector child, int index) {
        super(parent, child);
        this.index = index;
    }

    @Override
    public String getUnparsedSelector() {
        return "["+index+"]";
    }

    @Override
    protected Class<List> applyableTo() {
        return List.class;
    }

    @Override
    protected boolean existsImpl(List target) {
        return target.size() > index;
    }

    @Override
    protected void setImpl(List target, Object value) {
        target.set(index, value);
    }

    @Override
    protected Object getImpl(List target) {
        return target.get(index);
    }
}
