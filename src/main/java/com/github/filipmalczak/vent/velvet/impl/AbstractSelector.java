package com.github.filipmalczak.vent.velvet.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractSelector<Applyable> implements Selector{
    @Getter protected Selector parent;
    @Getter @Setter protected Selector child;

    protected void onlyApplyableTo(Class<Applyable> clazz, Object target){
        if (target == null || !clazz.isInstance(target))
            throw new SelectorNotApplyableException(getUnparsedSelector(), target);
    }

    protected abstract Class<Applyable> applyableTo();

    @Override
    public boolean exists(Object target) {
        try {
            onlyApplyableTo(applyableTo(), target);
            return existsImpl(applyableTo().cast(target));
        } catch (SelectorNotApplyableException e) {
            return false;
        }
    }

    protected abstract boolean existsImpl(Applyable target);

    @Override
    public void set(Object target, Object value) {
        onlyApplyableTo(applyableTo(), target);
        setImpl(applyableTo().cast(target), value);
    }

    protected abstract void setImpl(Applyable target, Object value);

    @Override
    public Object get(Object target) {
        onlyApplyableTo(applyableTo(), target);
        return getImpl(applyableTo().cast(target));
    }

    protected abstract Object getImpl(Applyable target);

    @Override
    public void delete(Object target) {
        onlyApplyableTo(applyableTo(), target);
        deleteImpl(applyableTo().cast(target));
    }

    protected abstract void deleteImpl(Applyable target);
}
