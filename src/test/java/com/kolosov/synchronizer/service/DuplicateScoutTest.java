package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.service.directOperations.DirectOperationsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DuplicateScoutTest {

    @Mock
    TreeService treeService;

    @Mock
    DirectOperationsService directOperationsService;

    @InjectMocks
    DuplicateScout duplicateScout;

    @Test
    void findDuplicateSyncs_treeNotSynchronized() {
        // setup
        when(treeService.isTreeSyncFullySynchronized(any())).thenReturn(false);

        // act
        // verify
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> duplicateScout.findDuplicateSyncs());
        assertEquals("Tree needs synchronization!", runtimeException.getMessage());
    }

    @Test
    void findDuplicateSyncs() {
        // setup
        TreeSync treeSync = new TreeSync(Location.PC);
        FolderSync folderSync = new FolderSync("folder", Location.PC, treeSync);
        FileSync fileSync1 = new FileSync("file1", Location.PC, folderSync);
        FileSync fileSync2 = new FileSync("file2", Location.PC, folderSync);
        FileSync fileSync3 = new FileSync("file3", Location.PC, folderSync);
        FileSync fileSync4 = new FileSync("file4", Location.PC, folderSync);

        when(treeService.getTreeSync()).thenReturn(treeSync);
        when(treeService.isTreeSyncFullySynchronized(treeSync)).thenReturn(true);
        when(directOperationsService.getSyncSize(any())).thenReturn(10L, 20L, 20L, 30L);
        when(directOperationsService.getMD5(any())).thenReturn("1", "1");

        // act
        List<List<FileSync>> duplicateSyncs = duplicateScout.findDuplicateSyncs();

        // verify
        assertEquals(1, duplicateSyncs.size());
        List<FileSync> duplicates = duplicateSyncs.get(0);
        assertSame(fileSync2, duplicates.get(0));
        assertSame(fileSync3, duplicates.get(1));
    }

}