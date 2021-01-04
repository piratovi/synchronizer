package com.kolosov.synchronizer.service.refresher;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.HistorySync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.enums.ProposedAction;
import com.kolosov.synchronizer.repository.SyncRepository;
import com.kolosov.synchronizer.service.TreeService;
import com.kolosov.synchronizer.service.directOperations.DirectOperationsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefresherTest {

    @Mock
    DirectOperationsService directOperationsService;

    @Mock
    SyncRepository syncRepository;

    @Mock
    TreeService treeService;

    @InjectMocks
    Refresher refresher;

    @Test
    void refresh() {
        // setup
        TreeSync oldTreeSync = new TreeSync(Location.PC);
        oldTreeSync.setSynchronized();
        FolderSync oldFolderSync1 = new FolderSync("folder1", Location.PC, oldTreeSync);
        oldFolderSync1.setSynchronized();
        FileSync oldFileSync1 = new FileSync("file1", Location.PC, oldFolderSync1);
        oldFileSync1.setSynchronized();
        FileSync oldFileSync2 = new FileSync("file2", Location.PC, oldFolderSync1);
        HistorySync historySync = new HistorySync(oldFileSync2, ProposedAction.TRANSFER);
        oldFileSync2.setHistorySync(historySync);

        // setup
        TreeSync newTreeSync = new TreeSync(Location.PC);
        newTreeSync.setSynchronized();
        FolderSync newFolderSync1 = new FolderSync("folder1", Location.PC, newTreeSync);
        newFolderSync1.setSynchronized();
        FileSync newFileSync1 = new FileSync("file1", Location.PHONE, newFolderSync1);
        FileSync newFileSync2 = new FileSync("file2", Location.PC, newFolderSync1);
        FileSync newFileSync3 = new FileSync("file3", Location.PC, newFolderSync1);

        when(directOperationsService.getNewTreeSync()).thenReturn(newTreeSync);
        when(treeService.findTreeSync()).thenReturn(Optional.of(oldTreeSync));

        // act
        refresher.refresh();

        // verify
        assertEquals(ProposedAction.REMOVE, newFileSync1.getHistorySync().action);
        assertEquals(ProposedAction.TRANSFER, newFileSync2.getHistorySync().action);
        assertEquals(ProposedAction.TRANSFER, newFileSync3.getHistorySync().action);
    }

}