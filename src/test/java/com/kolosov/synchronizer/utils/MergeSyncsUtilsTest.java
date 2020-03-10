package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.repository.TreeSyncRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MergeSyncsUtilsTest {

    @Autowired
    TreeSyncRepository repository;

    @Test
    @Transactional
    void mergeConflict() {
        TreeSync tree = repository.findAll().get(0);
        List<AbstractSync> flatList = SyncUtils.getFlatSyncs(tree.folderSyncs);
        Map<String, AbstractSync> oldFlatSyncs = flatList.stream()
                .collect(Collectors.toMap(sync -> sync.relativePath, Function.identity()));
    }
}