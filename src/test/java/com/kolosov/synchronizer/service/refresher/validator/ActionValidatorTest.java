package com.kolosov.synchronizer.service.refresher.validator;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.HistorySync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.enums.ProposedAction;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ActionValidatorTest {

    @Test
    void validate_syncAlreadySynchronized() {
        // setup
        FolderSync newSync = new FolderSync("folder", Location.PC, null);
        newSync.setSynchronized();

        // act
        ProposedAction result = ActionValidator.validate(newSync, Optional.empty());

        // verify
        assertEquals(ProposedAction.NOTHING, result);
    }

    @Test
    void validate_noOldSync() {
        // setup
        FolderSync newSync = new FolderSync("folder", Location.PC, null);

        // act
        ProposedAction result = ActionValidator.validate(newSync, Optional.empty());

        // verify
        assertEquals(ProposedAction.TRANSFER, result);
    }

    @Test
    void validate_oldSyncIsSynchronized() {
        // setup
        FolderSync newSync = new FolderSync("folder", Location.PC, null);
        FolderSync oldSync = new FolderSync("folder", Location.PC, null);
        oldSync.setSynchronized();

        // act
        ProposedAction result = ActionValidator.validate(newSync, Optional.of(oldSync));

        // verify
        assertEquals(ProposedAction.REMOVE, result);
    }

    @Test
    void validate_oldSyncHasAction() {
        // setup
        FolderSync newSync = new FolderSync("folder", Location.PC, null);
        FolderSync oldSync = new FolderSync("folder", Location.PC, null);
        HistorySync historySync = new HistorySync(oldSync, ProposedAction.TRANSFER);
        oldSync.setHistorySync(historySync);

        // act
        ProposedAction result = ActionValidator.validate(newSync, Optional.of(oldSync));

        // verify
        assertEquals(ProposedAction.TRANSFER, result);
    }

}