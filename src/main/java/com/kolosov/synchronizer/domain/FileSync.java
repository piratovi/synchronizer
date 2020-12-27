package com.kolosov.synchronizer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kolosov.synchronizer.enums.Location;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Entity
@NoArgsConstructor
public class FileSync extends Sync {

    @Transient
    public String ext;

    public FileSync(String name, Location location, FolderSync folder) {
        super(name, location, folder);
    }

    @Override
    @JsonIgnore
    public Stream<Sync> getNestedSyncs() {
        return Stream.of(this);
    }
}
