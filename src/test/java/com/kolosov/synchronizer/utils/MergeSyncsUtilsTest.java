package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.enums.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MergeSyncsUtilsTest {

    @Test
    void mergeTrees() {
        // setup
        TreeSync resultTree = new TreeSync(Location.PC);
        FolderSync folderSyncPc = new FolderSync("folder", Location.PC, resultTree);
        FileSync fileSyncPc = new FileSync("file", Location.PC, folderSyncPc);

        TreeSync treeToMerge = new TreeSync(Location.PHONE);
        FolderSync folderSyncPhone = new FolderSync("folder", Location.PHONE, treeToMerge);
        FileSync fileSyncPhone = new FileSync("file", Location.PHONE, folderSyncPhone);

        // act
        MergeSyncsUtils.mergeTrees(resultTree, treeToMerge);

        // verify
        assertTrue(resultTree.existOnPhone);
        assertTrue(folderSyncPc.existOnPhone);
        assertTrue(fileSyncPc.existOnPhone);
    }

    @Test
    void mergeSyncWithTree() {
        // setup
        TreeSync treeSyncPc = new TreeSync(Location.PC);
        FolderSync folderSyncPc = new FolderSync("folder", Location.PC, treeSyncPc);
        FileSync fileSyncPc = new FileSync("file", Location.PC, folderSyncPc);

        TreeSync treeSyncPhone = new TreeSync(Location.PHONE);
        FolderSync folderSyncPhone = new FolderSync("folder", Location.PHONE, treeSyncPhone);
        FileSync fileSyncPhone = new FileSync("file", Location.PHONE, folderSyncPhone);

        // act
        MergeSyncsUtils.mergeSyncWithTree(treeSyncPc, fileSyncPhone);

        // verify
        assertTrue(fileSyncPc.existOnPhone);
    }

    @Test
    void mergeSyncWithTree_addSyncToParentFolder() {
        // setup
        TreeSync treeSyncPc = new TreeSync(Location.PC);
        FolderSync folderSyncPc = new FolderSync("folder", Location.PC, treeSyncPc);
        FileSync fileSyncPc = new FileSync("file1", Location.PC, folderSyncPc);

        TreeSync treeSyncPhone = new TreeSync(Location.PHONE);
        FolderSync folderSyncPhone = new FolderSync("folder", Location.PHONE, treeSyncPhone);
        FileSync fileSyncPhone = new FileSync("file2", Location.PHONE, folderSyncPhone);

        // act
        MergeSyncsUtils.mergeSyncWithTree(treeSyncPc, fileSyncPhone);

        // verify
        assertEquals(folderSyncPc, fileSyncPhone.parent);
    }

}