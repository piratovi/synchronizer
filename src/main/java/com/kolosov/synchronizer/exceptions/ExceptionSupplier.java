package com.kolosov.synchronizer.exceptions;

import com.kolosov.synchronizer.domain.Sync;

import java.util.function.Supplier;

public class ExceptionSupplier {

    public static Supplier<SyncNotFoundException> syncNotFound(int id) {
        return () -> new SyncNotFoundException(String.format("Sync with id = %d not found", id));
    }

    //TODO попробовать еще варианты
    public static Supplier<SyncNotFoundException> syncNotFound(Sync sync) {
        return () -> new SyncNotFoundException(String.format("Sync %s not found", sync.relativePath));
    }

}
