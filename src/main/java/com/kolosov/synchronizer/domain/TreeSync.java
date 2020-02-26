package com.kolosov.synchronizer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class TreeSync {

    @Id
    @GeneratedValue
    @JsonIgnore
    public Long id;

    @OneToMany(cascade = {CascadeType.ALL})
    public List<FolderSync> folderSyncs = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    public List<HistorySync> historySyncs = new ArrayList<>();

    public TreeSync(List<FolderSync> folderSyncs) {
        this.folderSyncs = folderSyncs;
    }
}
