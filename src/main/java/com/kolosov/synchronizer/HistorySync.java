package com.kolosov.synchronizer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.enums.ProposedAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
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
