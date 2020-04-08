package com.kolosov.synchronizer.domain;

import com.kolosov.synchronizer.enums.Location;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class RootFolderSync extends FolderSync {

    public RootFolderSync(String relativePath, String name, Location location, FolderSync folder) {
        super(relativePath, name, location, folder);
    }
}