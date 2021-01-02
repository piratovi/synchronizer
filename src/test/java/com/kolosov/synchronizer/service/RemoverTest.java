package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.repository.SyncRepository;
import com.kolosov.synchronizer.service.directOperations.DirectOperationsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverTest {

    @Mock
    DirectOperationsService directOperationsService;

    @Mock
    SyncRepository syncRepository;

    @Mock
    TreeService treeService;

    @InjectMocks
    Remover remover;

    @Test
    void remove() {
        // setup
        TreeSync treeSync = new TreeSync(Location.PC);
        FolderSync folderSync1 = new FolderSync("folder1", Location.PC, treeSync);
        FolderSync folderSync2 = new FolderSync("folder2", Location.PC, treeSync);
        FolderSync subFolderSync = new FolderSync("subFolder", Location.PC, folderSync2);
        FileSync fileSync1 = new FileSync("file1", Location.PC, folderSync1);
        FileSync fileSync2 = new FileSync("file2", Location.PC, subFolderSync);
        FileSync fileSync3 = new FileSync("file3", Location.PC, subFolderSync);
        fileSync3.setSynchronized();

        when(syncRepository.findById(anyInt())).thenReturn(Optional.of(subFolderSync));
        when(treeService.getTreeSync()).thenReturn(treeSync);

        // act
        remover.remove(List.of(1));

        // verify
        assertNotNull(folderSync1.getParent());
        assertNotNull(subFolderSync.getParent());
        assertNull(fileSync2.getParent());
        assertNotNull(fileSync3.getParent());
    }
}