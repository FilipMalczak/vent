package com.github.filipmalczak.vent.velvet.impl;

public interface Selector {
    Selector getParent();

    //todo: if we keep all selectors in a SelectorList instead of keeping double linked list, we'll be able to do mkdir, mkdirs, etc
    Selector getChild();

    void setChild(Selector child);

    String getUnparsedSelector();

    boolean exists(Object target);

    void set(Object target, Object value);

    Object get(Object target);

    void delete(Object target);

    //todo: introduce null check on child

    default ByNameSelector byName(String name) {
        ByNameSelector child = new ByNameSelector(this, null, name);
        setChild(child);
        return child;
    }

    default ByIndexSelector byIndex(int index) {
        ByIndexSelector child = new ByIndexSelector(this, null, index);
        setChild(child);
        return child;
    }
}
