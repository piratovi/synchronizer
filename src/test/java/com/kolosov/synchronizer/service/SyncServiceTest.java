package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.Sync;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SyncServiceTest {

    @Autowired
    private SyncService syncService;

    @Test
    void integration_test() {
        // setup
        // act
        List<Sync> notSynchronizedSyncs = syncService.getNotSynchronizedSyncs();

        // verify
        assertEquals(1, notSynchronizedSyncs.size());
        Sync folderSync = notSynchronizedSyncs.get(0);
        assertEquals("\\\\Music Folder1", folderSync.getRelativePath());
        assertEquals(1, folderSync.asFolder().list.size());
        Sync fileSync = folderSync.asFolder().list.get(0);
        assertEquals("\\\\Music Folder1\\Composition 2", fileSync.getRelativePath());
    }

}