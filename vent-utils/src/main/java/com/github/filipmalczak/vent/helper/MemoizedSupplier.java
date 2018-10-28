package com.github.filipmalczak.vent.helper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Synchronized;

import java.util.Optional;
import java.util.function.Supplier;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemoizedSupplier<T> implements Supplier<T> {
    private Supplier<T> supplier;
    private Optional<T> value;

    @Override
    @Synchronized
    public T get() {
        if (!value.isPresent())
            value = Optional.of(supplier.get());
        return value.get();
    }

    public static <T> MemoizedSupplier<T> over(Supplier<T> supplier){
        return new MemoizedSupplier<>(supplier, Optional.empty());
    }
}
