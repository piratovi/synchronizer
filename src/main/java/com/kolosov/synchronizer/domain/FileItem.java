package com.kolosov.synchronizer.domain;

import lombok.ToString;

public class FileItem extends AbstractFile {

    public String ext;

    public FileItem(String relativePath, Location location) {
        super(relativePath, location);
    }

    @Override
    public String toString() {
        return "FileItem{" +
                relativePath + '\'' +
                '}';
    }
}
