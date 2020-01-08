package com.kolosov.synchronizer.domain;

import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@RequiredArgsConstructor
public enum Location {

    PC(Path.of("E:", "Music")),
    PHONE(null);

    public final Path path;
}
