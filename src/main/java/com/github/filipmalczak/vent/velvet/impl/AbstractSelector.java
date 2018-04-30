package com.github.filipmalczak.vent.velvet.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractSelector implements Selector{
    @Getter protected Selector parent;
    @Getter @Setter protected Selector child;

    protected void onlyApplyableTo(Class clazz, Object target){
        if (!clazz.isInstance(target))
            throw new RuntimeException("");//todo
    }
}
