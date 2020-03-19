package com.kolosov.synchronizer.exceptions;

import java.util.function.Supplier;

public class ExceptionSupplier {

    public static Supplier<SyncNotFoundException> syncNotFound(int id) {
        return () -> new SyncNotFoundException("Sync not found for id = " + id);
    }
}
