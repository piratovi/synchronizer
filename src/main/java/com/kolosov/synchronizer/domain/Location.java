package com.kolosov.synchronizer.domain;

import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.nio.file.Path;

@RequiredArgsConstructor
public enum Location {

    PC("E:\\Music"),
    PHONE("/Music");

    public final String rootPath;

    @PostConstruct
    public void setUp() {

    }
}
