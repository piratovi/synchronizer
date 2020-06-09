package com.kolosov.synchronizer.domain;

import com.kolosov.synchronizer.enums.Location;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class FileSync extends Sync {

    @Column(nullable = false)
    public String ext;

    public FileSync(@NonNull String relativePath, @NonNull String name, @NonNull Location location, @NonNull FolderSync folder) {
        super(relativePath, name, location, folder);
    }

    @Override
    public String toString() {
        return "FileSync. relativePath = " + relativePath;
    }
}
