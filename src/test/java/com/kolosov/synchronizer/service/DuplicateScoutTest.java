package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.repository.RootFolderSyncRepository;
import com.kolosov.synchronizer.utils.SyncUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DuplicateScoutTest {

    @Autowired
    DuplicateScout duplicateScout;

    @Autowired
    RootFolderSyncRepository rootFolderSyncRepository;

    @Test
    @Transactional
    void getDuplicates() {
        List<RootFolderSync> rootFolderSyncs = rootFolderSyncRepository.findAll();
        List<Sync> nested = rootFolderSyncs.stream()
                .flatMap(FolderSync::getNestedSyncs)
                .collect(Collectors.toList());

        List<Sync> listCount = SyncUtils.getFlatSyncs(rootFolderSyncs);

    assertEquals(listCount, nested);

//        List<Sync> duplicateSyncs = duplicateScout.findDuplicateSyncs();
    }

}