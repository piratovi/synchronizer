package com.kolosov.synchronizer.validators.action;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.HistorySync;
import com.kolosov.synchronizer.enums.ProposedAction;

import java.util.Map;
import java.util.Optional;

import static com.kolosov.synchronizer.enums.ProposedAction.TRANSFER;

public class ActionValidator {

    public static ProposedAction validate(AbstractSync newSync, Optional<HistorySync> oldHistorySyncOpt, Map<String, AbstractSync> oldFlatSyncs) {
        AbstractSync oldSync = oldFlatSyncs.get(newSync.relativePath);
        if (oldSync != null) {
            return OldSyncValidator.validate(newSync, oldHistorySyncOpt, oldFlatSyncs, oldSync);
        }
        return TRANSFER;
    }

}