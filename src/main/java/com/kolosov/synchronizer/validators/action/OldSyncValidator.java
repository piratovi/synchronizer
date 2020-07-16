package com.kolosov.synchronizer.validators.action;

import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.HistorySync;
import com.kolosov.synchronizer.enums.ProposedAction;

import java.util.Map;
import java.util.Optional;

import static com.kolosov.synchronizer.enums.ProposedAction.REMOVE;
import static com.kolosov.synchronizer.enums.ProposedAction.NOTHING;

public class OldSyncValidator {

    public static ProposedAction validate(Sync newSync, Optional<HistorySync> oldHistorySyncOpt, Map<String, Sync> oldFlatSyncs, Sync oldSync) {
        if (oldHistorySyncOpt.isPresent()) {
            return oldHistorySyncOpt.get().action;
        } else {
            if ((oldSync.isSynchronized()) && (newSync.isNotSynchronized())) {
                return REMOVE;
            }
            return NOTHING;
        }
    }

}
