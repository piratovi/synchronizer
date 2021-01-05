package com.kolosov.synchronizer.domain;

import com.kolosov.synchronizer.enums.Location;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.PrePersist;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class TreeSync extends FolderSync {

    public LocalDateTime created;

    public TreeSync(Location location) {
        super("\\", location, null);
    }

    @Override
    public void removeFromParent() {
        //Empty method
    }

    @PrePersist
    void onCreate() {
        this.created = LocalDateTime.now();
    }

}