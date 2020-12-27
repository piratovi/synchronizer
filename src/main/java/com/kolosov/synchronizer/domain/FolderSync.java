package com.kolosov.synchronizer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kolosov.synchronizer.enums.Location;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Entity
@NoArgsConstructor
public class FolderSync extends Sync {

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "parent", fetch = FetchType.EAGER)
    public List<Sync> list = new ArrayList<>();

    public FolderSync(String name, Location location, FolderSync parent) {
        super(name, location, parent);
    }

    protected void remove(Sync sync) {
        if (!list.contains(sync)) {
            throw new RuntimeException("Child Not Found In List");
        }
        list.remove(sync);
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
