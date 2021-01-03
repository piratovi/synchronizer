package com.kolosov.synchronizer.domain;

import com.kolosov.synchronizer.enums.Location;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class TreeSync extends FolderSync {

    public TreeSync(Location location) {
        super("\\", location, null);
    }

    @Override
    public void removeFromParent() {
        //Empty method
    }

}