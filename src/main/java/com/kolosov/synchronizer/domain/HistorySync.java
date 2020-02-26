package com.kolosov.synchronizer.domain;

import com.kolosov.synchronizer.enums.ProposedAction;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    public Long id;

    @OneToOne()
    public AbstractSync sync;

    public ProposedAction action;

    public HistorySync(AbstractSync sync, ProposedAction action) {
        this.sync = sync;
        this.action = action;
    }
}
