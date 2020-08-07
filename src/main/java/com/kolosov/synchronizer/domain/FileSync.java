package com.kolosov.synchronizer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kolosov.synchronizer.enums.Location;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.stream.Stream;

@Entity
@NoArgsConstructor
public class FileSync extends Sync implements Leaf {

    @Column(nullable = false)
    public String ext;

    public FileSync(String relativePath, String name, Location location, FolderSync folder) {
        super(relativePath, name, location, folder);
        this.ext = FilenameUtils.getExtension(relativePath).toLowerCase();
    }

    @Override
    public String toString() {
        return "FileSync. relativePath = " + relativePath;
    }

    @Override
    @JsonIgnore
    public Stream<Sync> getNestedSyncs() {
        return Stream.of(this);
    }
}
