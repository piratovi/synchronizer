package com.kolosov.synchronizer.domain;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

public class Folder extends AbstractFile {

    public List<AbstractFile> list = new ArrayList<>();

    public Folder(String relativePath, Location location) {
        super(relativePath, location);
    }

    @Override
    public String toString() {
        return "Folder{" +
                "relativePath='" + relativePath + '\'' +
                ", list=" + list +
                '}';
    }
}
