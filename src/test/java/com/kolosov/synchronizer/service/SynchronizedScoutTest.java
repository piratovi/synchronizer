package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.enums.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SynchronizedScoutTest {

    @Mock
    TreeService treeService;

    @Mock
    EntityManager entityManager;

    @InjectMocks
    SynchronizedScout synchronizedScout;

    @Test
    void findNotSynchronizedSyncs() {
        // setup
        TreeSync treeSync = new TreeSync(Location.PC);
        treeSync.setSynchronized();
        FolderSync folderSync1 = new FolderSync("folder1", Location.PC, treeSync);
        folderSync1.setSynchronized();
        FileSync fileSync1 = new FileSync("file1", Location.PC, folderSync1);
        fileSync1.setSynchronized();
        FolderSync folderSync2 = new FolderSync("folder2", Location.PC, treeSync);
        folderSync2.setSynchronized();
        FolderSync subFolderSync = new FolderSync("subFolder", Location.PC, folderSync2);
        subFolderSync.setSynchronized();
        FileSync fileSync2 = new FileSync("file2", Location.PC, subFolderSync);
        FileSync fileSync3 = new FileSync("file3", Location.PC, subFolderSync);

        when(treeService.getTreeSync()).thenReturn(treeSync);

        // act
        TreeSync notSynchronizedSyncs = synchronizedScout.getNotSynchronizedSyncs();

        // verify
        assertSame(treeSync, notSynchronizedSyncs);
        assertEquals(5, treeSync.getNestedSyncs().count());
        assertNull(folderSync1.getParent());
        assertSame(treeSync, folderSync2.getParent());
        assertSame(folderSync2, subFolderSync.getParent());
        assertSame(subFolderSync, fileSync2.getParent());
        assertSame(subFolderSync, fileSync3.getParent());
    }

}