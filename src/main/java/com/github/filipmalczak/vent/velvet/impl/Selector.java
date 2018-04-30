package com.github.filipmalczak.vent.velvet.impl;

public interface Selector {
    Selector getParent();
    Selector getChild();
    void setChild(Selector child);

    boolean exists(Object target);
    void set(Object target, Object value);
    Object get(Object target);

    //todo: introduce null check on child

    default ByNameSelector byName(String name){
        ByNameSelector child = new ByNameSelector(this, null, name);
        setChild(child);
        return child;
    }

    default ByIndexSelector byIndex(int index){
        ByIndexSelector child = new ByIndexSelector(this, null, index);
        setChild(child);
        return child;
    }
}
