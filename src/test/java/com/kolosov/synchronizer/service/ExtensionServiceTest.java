package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.dto.ExtensionStat;
import com.kolosov.synchronizer.enums.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExtensionServiceTest {

    @Mock
    TreeService treeService;

    @InjectMocks
    ExtensionService extensionService;

    @Test
    void getExtensionStats() {
        // setup
        TreeSync resultTree = new TreeSync(Location.PC);
        FolderSync folderSyncPc = new FolderSync("folder", Location.PC, resultTree);
        FileSync fileSyncPc1 = new FileSync("file1.mp3", Location.PC, folderSyncPc);
        FileSync fileSyncPc2 = new FileSync("file2.txt", Location.PC, folderSyncPc);
        FileSync fileSyncPc3 = new FileSync("file3.txt", Location.PC, folderSyncPc);
        FileSync fileSyncPc4 = new FileSync("file4.flac", Location.PC, folderSyncPc);

        when(treeService.getTreeSync()).thenReturn(resultTree);

        // act
        List<ExtensionStat> extensionStats = extensionService.getExtensionStats();

        // verify
        assertEquals("txt", extensionStats.get(0).name);
        assertEquals(2, extensionStats.get(0).count);
        assertEquals("mp3", extensionStats.get(1).name);
        assertEquals(1, extensionStats.get(1).count);
        assertEquals("flac", extensionStats.get(2).name);
        assertEquals(1, extensionStats.get(2).count);
    }

    @Test
    void getExtensionStats_emptyTree() {
        // setup
        TreeSync resultTree = new TreeSync(Location.PC);
        FolderSync folderSyncPc = new FolderSync("folder", Location.PC, resultTree);

        when(treeService.getTreeSync()).thenReturn(resultTree);

        // act
        List<ExtensionStat> extensionStats = extensionService.getExtensionStats();

        // verify
        assertTrue(extensionStats.isEmpty());
    }
}