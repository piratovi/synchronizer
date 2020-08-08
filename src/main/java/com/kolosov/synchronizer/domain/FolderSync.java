package com.kolosov.synchronizer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kolosov.synchronizer.enums.Location;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Entity
@NoArgsConstructor
public class FolderSync extends Sync implements Composite {

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "parent", fetch = FetchType.EAGER)
    public List<Sync> list = new ArrayList<>();

    public FolderSync(String name, Location location, FolderSync parent) {
        super(name, location, parent);
    }

    @Override
    public String toString() {
        return "FolderSync. relativePath = " + relativePath;
    }

    public void remove(Sync sync) {
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
    public boolean hasSyncs() {
        return !list.isEmpty();
    }

    @JsonIgnore
    public void add(Sync sync) {
        list.add(sync);
        sync.parent = this;
    }

    @Override
    @JsonIgnore
    public Stream<Sync> getNestedSyncs() {
        Stream<Sync> stream = Stream.of(this);
        for (Sync sync : list) {
            stream = Stream.concat(stream, sync.getNestedSyncs());
        }
        return stream;
    }
}
