package com.kolosov.synchronizer.domain;

import com.kolosov.synchronizer.enums.Location;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class FileSync extends AbstractSync {

    @NonNull
    public String ext;

    public FileSync(String relativePath, String name, Location location, FolderSync folder) {
        super(relativePath, name, location, folder);
    }

    @Override
    public String toString() {
        return "FileSync{" +
                "relativePath='" + relativePath + '\'' +
                '}';
    }
}
