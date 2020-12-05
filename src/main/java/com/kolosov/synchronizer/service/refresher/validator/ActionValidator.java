package com.kolosov.synchronizer.service.refresher.validator;

import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.enums.ProposedAction;

import java.util.Optional;

import static com.kolosov.synchronizer.enums.ProposedAction.NOTHING;
import static com.kolosov.synchronizer.enums.ProposedAction.REMOVE;
import static com.kolosov.synchronizer.enums.ProposedAction.TRANSFER;

public class ActionValidator {

    public static ProposedAction validate(Sync newSync, Optional<Sync> oldSyncOpt) {
        if (newSync.isSynchronized()) {
            return NOTHING;
        }
        if (oldSyncOpt.isPresent()) {
            Sync oldSync = oldSyncOpt.get();
            if (oldSync.isNotSynchronized()) {
                return oldSync.getHistorySync().getAction();
            }
            return REMOVE;
        }
        return TRANSFER;
    }

}