package com.kolosov.synchronizer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kolosov.synchronizer.enums.Location;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.io.FilenameUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class FileSync extends Sync {

    @Column(nullable = false)
    public String ext;

    public FileSync(@NonNull String relativePath, @NonNull String name, @NonNull Location location, @NonNull FolderSync folder) {
        super(relativePath, name, location, folder);
        this.ext = FilenameUtils.getExtension(relativePath).toLowerCase();
    }

    @Override
    public String toString() {
        return "FileSync. relativePath = " + relativePath;
    }

    @Override
    @JsonIgnore
    public List<Sync> getNestedSyncs() {
        return new ArrayList<>(List.of(this));
    }
}
