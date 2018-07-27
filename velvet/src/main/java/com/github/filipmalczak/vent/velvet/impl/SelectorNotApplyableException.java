package com.github.filipmalczak.vent.velvet.impl;

public class SelectorNotApplyableException extends RuntimeException {
    private String unparsedSelector;
    private Object target;

    public SelectorNotApplyableException(String unparsedSelector, Object target) {
        super(getExceptionMessage(unparsedSelector, target));
        this.unparsedSelector = unparsedSelector;
        this.target = target;
    }

    public SelectorNotApplyableException(Throwable cause, String unparsedSelector, Object target) {
        super(getExceptionMessage(unparsedSelector, target), cause);
        this.unparsedSelector = unparsedSelector;
        this.target = target;
    }

    private static String getExceptionMessage(String unparsedSelector, Object target) {
        return "Selector target" + unparsedSelector + " cannot be applied to target=" + target + (target == null ? "" : " of type " + target.getClass());
    }
}
