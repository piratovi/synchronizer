package com.kolosov.synchronizer.service.refresher.validator;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.enums.ProposedAction;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@SpringBootTest
class ActionValidatorTest {

//    @Test
//    void validate_syncAlreadySynchronized() {
//        // setup
//        FolderSync folderSync = new FolderSync("", Location.PC, null);
//        folderSync.setExistOnPhone(true);
//
//        // act
//        ProposedAction result = ActionValidator.validate(folderSync, null.get(folderSync.relativePath));
//
//        // verify
//        assertEquals(ProposedAction.NOTHING, result);
//    }
//
//    @Test
//    void validate_oldValidator() {
//        // setup
//        FolderSync oldSync = new FolderSync("\\", Location.PC, null);
//        FolderSync folderSync = new FolderSync("\\", Location.PC, null);
//
//        // act
//        ProposedAction result = ActionValidator.validate(folderSync, Map.of("\\", oldSync).get(folderSync.relativePath));
//
//        // verify
//        assertEquals(ProposedAction.NOTHING, result);
//    }
//
//    @Test
//    void validate_syncIsNew() {
//        // setup
//        FolderSync folderSync = new FolderSync("", Location.PC, null);
//
//        // act
//        ProposedAction result = ActionValidator.validate(folderSync, Collections.emptyMap().get(folderSync.relativePath));
//
//        // verify
//        assertEquals(ProposedAction.TRANSFER, result);
//    }
}