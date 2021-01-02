package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.repository.TreeSyncRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TreeServiceTest {

    @Mock
    TreeSyncRepository treeSyncRepository;

    @InjectMocks
    TreeService treeService;

    @Test
    void findTreeSync_NoTreeInDB() {
        // setup
        when(treeSyncRepository.findAll()).thenReturn(Collections.emptyList());

        // act
        Optional<TreeSync> result = treeService.findTreeSync();

        // verify
        assertTrue(result.isEmpty());
    }

    @Test
    void findTreeSync_moreThanOneTree() {
        // setup
        TreeSync treeSync1 = new TreeSync();
        TreeSync treeSync2 = new TreeSync();

        when(treeSyncRepository.findAll()).thenReturn(List.of(treeSync1, treeSync2));

        // act
        // verify
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> treeService.findTreeSync());
        assertEquals("More than 1 tree in DB", runtimeException.getMessage());
    }

    @Test
    void findTreeSync() {
        // setup
        TreeSync treeSync = new TreeSync();

        when(treeSyncRepository.findAll()).thenReturn(List.of(treeSync));

        // act
        Optional<TreeSync> result = treeService.findTreeSync();

        // verify
        assertTrue(result.isPresent());
        assertEquals(treeSync, result.get());
    }

    @Test
    void getTreeSync_NoTreeInDB() {
        // setup
        when(treeSyncRepository.findAll()).thenReturn(Collections.emptyList());

        // act
        // verify
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> treeService.getTreeSync());
        assertEquals("No tree in DB", runtimeException.getMessage());
    }

    @Test
    void isTreeSyncFullySynchronized() {
        // setup
        TreeSync treeSync = new TreeSync(Location.PC);
        treeSync.setSynchronized();
        FolderSync folderSync = new FolderSync("folder", Location.PC, treeSync);
        folderSync.setSynchronized();
        // act
        boolean result = treeService.isTreeSyncFullySynchronized(treeSync);

        // verify
        assertTrue(result);
    }

    @Test
    void isTreeSyncFullySynchronized_false() {
        // setup
        TreeSync treeSync = new TreeSync(Location.PC);
        FolderSync folderSync = new FolderSync("folder", Location.PC, treeSync);
        // act
        boolean result = treeService.isTreeSyncFullySynchronized(treeSync);

        // verify
        assertFalse(result);
    }

}