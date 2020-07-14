package com.kolosov.synchronizer.validators.action;

import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.HistorySync;
import com.kolosov.synchronizer.enums.ProposedAction;

import java.util.Map;
import java.util.Optional;

import static com.kolosov.synchronizer.enums.ProposedAction.NOTHING;
import static com.kolosov.synchronizer.enums.ProposedAction.TRANSFER;

public class ActionValidator {

    public static ProposedAction validate(Sync newSync, Optional<HistorySync> oldHistorySyncOpt, Map<String, Sync> mappedOldSyncs) {
        if (newSync.isSynchronized()) {
            return NOTHING;
        }
        Sync oldSync = mappedOldSyncs.get(newSync.relativePath);
        if (oldSync != null) {
            return OldSyncValidator.validate(newSync, oldHistorySyncOpt, mappedOldSyncs, oldSync);
        }
        if (newSync.isNotSynchronized()) {
            return TRANSFER;
        }
        return NOTHING;
    }

}