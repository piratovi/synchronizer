package com.kolosov.synchronizer.domain;

import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@RequiredArgsConstructor
public enum Location {

    PC(Path.of("D:", "Music")),
    PHONE(Path.of("Z:", "Music"));

    public final Path path;
}
