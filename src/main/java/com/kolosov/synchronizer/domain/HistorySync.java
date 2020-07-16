package com.kolosov.synchronizer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kolosov.synchronizer.enums.ProposedAction;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Data
@NoArgsConstructor
public class HistorySync {

    @Id
    @GeneratedValue
    @JsonIgnore
    public Integer id;

    @OneToOne()
    @JsonIgnore
    public Sync sync;

    public ProposedAction action;

    public HistorySync(Sync sync, ProposedAction action) {
        this.sync = sync;
        this.action = action;
    }
}
