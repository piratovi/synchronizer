package com.kolosov.synchronizer.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class TreeSync {

    @Id
    @GeneratedValue
    public Long id;

    @OneToMany(cascade = {CascadeType.ALL})
    public List<FolderSync> folderSyncs;

    public TreeSync(List<FolderSync> folderSyncs) {
        this.folderSyncs = folderSyncs;
    }
}
