package com.kolosov.synchronizer.dto;

import com.kolosov.synchronizer.domain.HistorySync;
import com.kolosov.synchronizer.enums.ProposedAction;

public class HistorySyncDTO {

    public Integer syncId;
    public ProposedAction action;
    public String relativePath;

    public HistorySyncDTO(HistorySync historySync) {
        this.syncId = historySync.sync.id;
        this.action = historySync.action;
        this.relativePath = historySync.sync.relativePath;
    }
}
