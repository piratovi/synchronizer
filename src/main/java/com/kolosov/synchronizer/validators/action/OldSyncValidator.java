package com.kolosov.synchronizer.validators.action;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.HistorySync;
import com.kolosov.synchronizer.enums.ProposedAction;

import java.util.Map;
import java.util.Optional;

import static com.kolosov.synchronizer.enums.ProposedAction.DELETE;
import static com.kolosov.synchronizer.enums.ProposedAction.NOTHING;

public class OldSyncValidator {

    public static ProposedAction validate(AbstractSync newSync, Optional<HistorySync> oldHistorySyncOpt, Map<String, AbstractSync> oldFlatSyncs, AbstractSync oldSync) {
        if (oldHistorySyncOpt.isPresent()) {
            return oldHistorySyncOpt.get().action;
        } else {
            if ((oldSync.isSynchronized()) && (newSync.isNotSynchronized())) {
                return DELETE;
            }
            return NOTHING;
        }
    }

}