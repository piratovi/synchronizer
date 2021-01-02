package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.enums.Location;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SyncUtilsTest {

    @Test
    void findSync() {
        // setup
        FolderSync searchLocation = new FolderSync("folder", Location.PC, null);
        FileSync fileSync = new FileSync("file", Location.PC, searchLocation);

        FolderSync folderSync = new FolderSync("folder", Location.PHONE, null);
        FileSync searchTarget = new FileSync("file", Location.PHONE, folderSync);

        // act
        Optional<Sync> result = SyncUtils.findSync(searchLocation, searchTarget);

        // verify
        assertTrue(result.isPresent());
        assertSame(fileSync, result.get());
    }

    @Test
    void findSync_notFound() {
        // setup
        FolderSync searchLocation = new FolderSync("folder", Location.PC, null);
        FileSync fileSync = new FileSync("file", Location.PC, searchLocation);

        FolderSync folderSync = new FolderSync("folder", Location.PHONE, null);
        FileSync searchTarget = new FileSync("file1", Location.PHONE, folderSync);

        // act
        Optional<Sync> result = SyncUtils.findSync(searchLocation, searchTarget);

        // verify
        assertTrue(result.isEmpty());
    }

    @Test
    void getParents() {
        // setup
        FolderSync rootFolderSync = new FolderSync("rootFolder", Location.PC, null);
        FolderSync subFolderSync = new FolderSync("subFolder", Location.PC, rootFolderSync);
        FileSync fileSync = new FileSync("file", Location.PC, subFolderSync);

        // act
        List<FolderSync> result = SyncUtils.getParents(fileSync);

        // verify
        assertEquals(2, result.size());
        assertSame(rootFolderSync, result.get(0));
        assertSame(subFolderSync, result.get(1));
    }

    @Test
    void getParents_empty() {
        // setup
        FileSync fileSync = new FileSync("file", Location.PC, null);

        // act
        List<FolderSync> result = SyncUtils.getParents(fileSync);

        // verify
        assertTrue(result.isEmpty());
    }

    @Test
    void getEmptyFolders() {
        // setup
        TreeSync treeSync = new TreeSync(Location.PC);
        FolderSync folderSync = new FolderSync("folder", Location.PC, treeSync);

        // act
        List<FolderSync> result = SyncUtils.getEmptyFolders(treeSync);

        // verify
        assertEquals(1, result.size());
        assertSame(folderSync, result.get(0));
    }

    @Test
    void getEmptyFolders_empty() {
        // setup
        TreeSync treeSync = new TreeSync(Location.PC);
        FolderSync folderSync = new FolderSync("folder", Location.PC, treeSync);
        FileSync fileSync = new FileSync("file", Location.PC, folderSync);

        // act
        List<FolderSync> result = SyncUtils.getEmptyFolders(treeSync);

        // verify
        assertEquals(0, result.size());
    }

    @Test
    void getFoldersWithoutNestedFiles_RootFoldersNotInclude() {
        // setup
        TreeSync treeSync = new TreeSync(Location.PC);
        FolderSync folderSync1 = new FolderSync("folder1", Location.PC, treeSync);
        FolderSync folderSync2 = new FolderSync("folder2", Location.PC, treeSync);
        FolderSync folderSync3 = new FolderSync("folder3", Location.PC, treeSync);
        FolderSync folderSync4 = new FolderSync("folder4", Location.PC, treeSync);
        FolderSync subFolderSync1 = new FolderSync("subFolder1", Location.PC, folderSync2);
        FolderSync subFolderSync2 = new FolderSync("subFolder2", Location.PC, folderSync3);
        FolderSync subFolderSync3 = new FolderSync("subFolder3", Location.PC, folderSync4);
        FileSync fileSync1 = new FileSync("file1", Location.PC, folderSync1);
        FileSync fileSync2 = new FileSync("file2", Location.PC, subFolderSync1);

        // act
        List<FolderSync> result = SyncUtils.getFoldersWithoutNestedFiles(treeSync, false);

        // verify
        assertEquals(2, result.size());
        assertSame(subFolderSync2, result.get(0));
        assertSame(subFolderSync3, result.get(1));
    }

    @Test
    void getFoldersWithoutNestedFiles_RootFoldersInclude() {
        // setup
        TreeSync treeSync = new TreeSync(Location.PC);
        FolderSync folderSync1 = new FolderSync("folder1", Location.PC, treeSync);
        FolderSync folderSync2 = new FolderSync("folder2", Location.PC, treeSync);
        FolderSync folderSync3 = new FolderSync("folder3", Location.PC, treeSync);
        FolderSync folderSync4 = new FolderSync("folder4", Location.PC, treeSync);
        FolderSync subFolderSync1 = new FolderSync("subFolder1", Location.PC, folderSync2);
        FolderSync subFolderSync2 = new FolderSync("subFolder2", Location.PC, folderSync3);
        FolderSync subFolderSync3 = new FolderSync("subFolder3", Location.PC, folderSync4);
        FileSync fileSync1 = new FileSync("file1", Location.PC, folderSync1);
        FileSync fileSync2 = new FileSync("file2", Location.PC, subFolderSync1);

        // act
        List<FolderSync> result = SyncUtils.getFoldersWithoutNestedFiles(treeSync, true);

        // verify
        assertEquals(4, result.size());
        assertSame(folderSync3, result.get(0));
        assertSame(subFolderSync2, result.get(1));
        assertSame(folderSync4, result.get(2));
        assertSame(subFolderSync3, result.get(3));
    }

}