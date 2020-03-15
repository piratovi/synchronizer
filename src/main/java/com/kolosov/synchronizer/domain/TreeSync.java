package com.kolosov.synchronizer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    public Integer id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    public List<FolderSync> folderSyncs = new ArrayList<>();

    public TreeSync(List<FolderSync> folderSyncs) {
        this.folderSyncs = folderSyncs;
    }
}
