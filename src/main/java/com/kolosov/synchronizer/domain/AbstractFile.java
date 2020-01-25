package com.kolosov.synchronizer.domain;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
public abstract class AbstractFile {

    public final String relativePath;

    public final Location location;


}
