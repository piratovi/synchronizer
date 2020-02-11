package com.kolosov.synchronizer.domain;

import com.kolosov.synchronizer.enums.Location;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class FolderSync extends AbstractSync {

    @OneToMany
    public List<AbstractSync> list = new ArrayList<>();

    public FolderSync(String relativePath, String name, Location location) {
        super(relativePath, name, location);
    }

    @Override
    public String toString() {
        return "Folder{" +
                "relativePath='" + relativePath + '\'' +
                ", list=" + list +
                '}';
    }
}
