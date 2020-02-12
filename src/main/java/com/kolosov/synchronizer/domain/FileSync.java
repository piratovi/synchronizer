package com.kolosov.synchronizer.domain;

import com.kolosov.synchronizer.enums.Location;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
public class FileSync extends AbstractSync {

    @Column
    public String ext;

    public FileSync(String relativePath, String name, Location location, FolderSync folder) {
        super(relativePath, name, location, folder);
    }

    @Override
    public String toString() {
        return "FileItem{" +
                relativePath + '\'' +
                '}';
    }
}
