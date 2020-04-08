package com.kolosov.synchronizer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kolosov.synchronizer.enums.Location;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class FolderSync extends AbstractSync {

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "parent")
    public List<AbstractSync> list = new ArrayList<>();

    public FolderSync(String relativePath, String name, Location location, FolderSync folder) {
        super(relativePath, name, location, folder);
    }

    @Override
    public String toString() {
        return "FolderSync{" +
                "relativePath='" + relativePath + '\'' +
                '}';
    }

    public void remove(AbstractSync sync) {
        if (!list.remove(sync)) {
            throw new RuntimeException("Child Not Found In List");
        }
        sync.parent = null;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @JsonIgnore
    public boolean hasFiles() {
        return !list.isEmpty();
    }

    @JsonIgnore
    public void add(AbstractSync sync) {
        list.add(sync);
        sync.parent = this;
    }
}