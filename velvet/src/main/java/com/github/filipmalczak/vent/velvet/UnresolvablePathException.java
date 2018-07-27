package com.github.filipmalczak.vent.velvet;

import lombok.Getter;

public class UnresolvablePathException extends RuntimeException {
    @Getter
    private String path;
    @Getter
    private Object target;

    public UnresolvablePathException(String path, Object target) {
        super(getExceptionMessage(path, target));
        this.path = path;
        this.target = target;
    }

    public UnresolvablePathException(Throwable cause, String path, Object target) {
        super(getExceptionMessage(path, target), cause);
        this.path = path;
        this.target = target;
    }

    private static String getExceptionMessage(String path, Object target) {
        return "Path " + path + " is not resolvable in context of " + target;
    }
}
