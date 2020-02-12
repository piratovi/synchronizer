package com.kolosov.synchronizer.domain;

import com.kolosov.synchronizer.enums.Location;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class FolderSync extends AbstractSync {

    @OneToMany(cascade = {CascadeType.ALL})
    public List<AbstractSync> list = new ArrayList<>();

    public FolderSync(String relativePath, String name, Location location, FolderSync folder) {
        super(relativePath, name, location, folder);
    }

}
