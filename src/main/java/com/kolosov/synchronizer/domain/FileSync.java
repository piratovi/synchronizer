package com.kolosov.synchronizer.domain;

import com.kolosov.synchronizer.enums.Location;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class FileSync extends Sync {

    @Column(nullable = false)
    public String ext;

    public FileSync(String relativePath, String name, Location location, FolderSync folder) {
        super(relativePath, name, location, folder);
    }

    @Override
    public String toString() {
        return "FileSync. relativePath = " + relativePath;
    }
}
