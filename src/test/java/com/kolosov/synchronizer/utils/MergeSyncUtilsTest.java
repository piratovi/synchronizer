package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.enums.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MergeSyncUtilsTest {

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
        MergeSyncUtils.mergeTrees(resultTree, treeToMerge);

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
        MergeSyncUtils.mergeSyncWithTree(treeSyncPc, fileSyncPhone);

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
        MergeSyncUtils.mergeSyncWithTree(treeSyncPc, fileSyncPhone);

        // verify
        assertSame(folderSyncPc, fileSyncPhone.parent);
    }

    @Test
    void mergeTrees_resultTreeIsEmpty() {
        // setup
        TreeSync treeSyncPc = new TreeSync(Location.PC);
        FolderSync folderSyncPc = new FolderSync("folder", Location.PC, treeSyncPc);

        TreeSync treeSyncPhone = new TreeSync(Location.PHONE);
        FolderSync folderSyncPhone1 = new FolderSync("folder", Location.PHONE, treeSyncPhone);
        FolderSync folderSyncPhone2 = new FolderSync("folder2", Location.PHONE, folderSyncPhone1);
        FileSync fileSyncPhone = new FileSync("file", Location.PHONE, folderSyncPhone2);

        // act
        MergeSyncUtils.mergeTrees(treeSyncPc, treeSyncPhone);

        // verify
        assertSame(folderSyncPc, folderSyncPhone2.getParent());
        assertSame(folderSyncPc, fileSyncPhone.getParent().getParent());
    }

}