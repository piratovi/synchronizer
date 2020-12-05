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
        FileSync searchTarget = new FileSync("file", Location.PHONE, null);
        searchTarget.relativePath = "folder\\file";

        // act
        Optional<Sync> result = SyncUtils.findSync(searchLocation, searchTarget);

        // verify
        assertTrue(result.isPresent());
        assertEquals(fileSync, result.get());
    }

    @Test
    void findSync_notFound() {
        // setup
        FolderSync searchLocation = new FolderSync("folder", Location.PC, null);
        FileSync fileSync = new FileSync("file", Location.PC, searchLocation);
        FileSync searchTarget = new FileSync("file1", Location.PHONE, null);
        searchTarget.relativePath = "folder\\file1";

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
        assertEquals(rootFolderSync, result.get(0));
        assertEquals(subFolderSync, result.get(1));
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
        assertEquals(folderSync, result.get(0));
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
}