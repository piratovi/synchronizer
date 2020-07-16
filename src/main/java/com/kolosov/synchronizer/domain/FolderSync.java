package com.kolosov.synchronizer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kolosov.synchronizer.enums.Location;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class FolderSync extends Sync {

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "parent")
    public List<Sync> list = new ArrayList<>();

    ////TODO переделать в трансферобжект
    @Transient
    public int rememberedChildQuantity;

    public FolderSync(@NonNull String relativePath, @NonNull String name, @NonNull Location location, @NonNull FolderSync parent) {
        super(relativePath, name, location, parent);
    }

    protected FolderSync(@NonNull String relativePath, @NonNull String name, @NonNull Location location) {
        super(relativePath, name, location);
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
    public List<Sync> getNestedSyncs() {
        List<Sync> result = new ArrayList<>();
        result.add(this);
        list.forEach(sync -> result.addAll(sync.getNestedSyncs()));
        return result;
    }
}
