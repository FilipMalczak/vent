package com.github.filipmalczak.vent.velvet;

import com.github.filipmalczak.vent.velvet.impl.Selector;
import com.github.filipmalczak.vent.velvet.impl.SelectorNotApplyableException;

import java.util.List;

public interface UnboundPath {
    /**
     * Full path and not the last part
     */
    String getPath();

    /**
     * Last element of this will be the same as getPath()
     */
    List<String> getSuperPaths();

    default boolean exists(Object target) {
        return bind(target).exists();
    }

    default void set(Object target, Object value) {
        bind(target).set(value);
    }

    //it just begs to return Optional from here, but we want to differentiate between missing path and null value
    default Object get(Object target) {
        return bind(target).get();
    }

    default void delete(Object target) {
        bind(target).delete();
    }

    Selector getRootSelector();

    default BoundPath bind(Object target) {
        return new BoundPath() {
            Selector rootSelector = getRootSelector();

            @Override
            public String getPath() {
                return UnboundPath.this.getPath();
            }

            @Override
            public Object getTarget() {
                return target;
            }

            @Override
            public boolean exists() {
                Selector currentSelector = rootSelector;
                Object currentTarget = target;
                while (currentSelector != null) {
                    if (!currentSelector.exists(currentTarget))
                        return false;
                    currentTarget = currentSelector.get(currentTarget);
                    currentSelector = currentSelector.getChild();
                }
                return true;
            }

            @Override
            public void set(Object value) {
                try {
                    Selector parentSelector = null;
                    Selector currentSelector = rootSelector;
                    Object parentTarget = null;
                    Object currentTarget = target;
                    while (currentSelector != null) { // go to the end of the chain
                        boolean lastOne = false;
                        if (!currentSelector.exists(currentTarget)) // is allowed not to exist only for last selector
                            if (currentSelector.getChild() != null) // and only the last one will have no child
                                throw new UnresolvablePathException(getPath(), target);
                            else
                                lastOne = true;
                        parentTarget = currentTarget;
                        currentTarget = lastOne ? null : currentSelector.get(currentTarget);
                        parentSelector = currentSelector;
                        currentSelector = currentSelector.getChild();
                    }
                    //go back a level, currentSelector = null, so we need parents
                    //at this point currentTarget is the value that will be replaced (possibly null)
                    currentSelector = parentSelector;
                    currentTarget = parentTarget;
                    currentSelector.set(currentTarget, value);
                } catch (SelectorNotApplyableException e) {
                    throw new UnresolvablePathException(e, getPath(), target);
                }
            }

            @Override
            public Object get() {
                try {
                    Selector currentSelector = rootSelector;
                    Object currentTarget = target;
                    while (currentSelector != null) {
                        if (!currentSelector.exists(currentTarget))
                            throw new UnresolvablePathException(getPath(), target);
                        currentTarget = currentSelector.get(currentTarget);
                        currentSelector = currentSelector.getChild();
                    }
                    return currentTarget;
                } catch (SelectorNotApplyableException e) {
                    throw new UnresolvablePathException(e, getPath(), target);
                }
            }

            @Override
            public void delete() {
                //fixme raw copypaste from set() - proper refactor is in place
                //fixme delete for non-existent - what should happen?
                //todo test delete
                try {
                    Selector parentSelector = null;
                    Selector currentSelector = rootSelector;
                    Object parentTarget = null;
                    Object currentTarget = target;
                    while (currentSelector != null) { // go to the end of the chain
                        boolean lastOne = false;
                        if (!currentSelector.exists(currentTarget)) // is allowed not to exist only for last selector
                            if (currentSelector.getChild() != null) // and only the last one will have no child
                                throw new UnresolvablePathException(getPath(), target);
                            else
                                lastOne = true;
                        parentTarget = currentTarget;
                        currentTarget = lastOne ? null : currentSelector.get(currentTarget);
                        parentSelector = currentSelector;
                        currentSelector = currentSelector.getChild();
                    }
                    //go back a level, currentSelector = null, so we need parents
                    //at this point currentTarget is the value that will be replaced (possibly null)
                    currentSelector = parentSelector;
                    currentTarget = parentTarget;
                    currentSelector.delete(currentTarget);
                } catch (SelectorNotApplyableException e) {
                    throw new UnresolvablePathException(e, getPath(), target);
                }
            }
        };
    }
}
