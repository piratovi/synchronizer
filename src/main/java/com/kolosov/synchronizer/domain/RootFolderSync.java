package com.kolosov.synchronizer.domain;

import com.kolosov.synchronizer.enums.Location;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class RootFolderSync extends FolderSync {

    public RootFolderSync(@NonNull String relativePath, @NonNull String name, @NonNull Location location) {
        super(relativePath, name, location);
    }

    @Override
    public void removeFromParent() {
        //Empty method
    }
}
