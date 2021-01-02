package com.kolosov.synchronizer.service;


import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.service.directOperations.DirectOperationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DuplicateScout {

    private final TreeService treeService;
    private final DirectOperationsService directOperationsService;

    public List<List<FileSync>> findDuplicateSyncs() {
        TreeSync treeSync = treeService.getTreeSync();
        if (!treeService.isTreeSyncFullySynchronized(treeSync)) {
            throw new RuntimeException("Tree needs synchronization!");
        }
        List<List<FileSync>> fileSyncsGroupedBySize = getFileSyncsGroupedBySize(treeSync);
        return getFileSyncsGroupedByMD5(fileSyncsGroupedBySize);
    }

    private List<List<FileSync>> getFileSyncsGroupedBySize(TreeSync treeSync) {
        return treeSync.getNestedSyncs()
                .filter(Sync::isFile)
                .map(Sync::asFile)
                .collect(Collectors.groupingBy(directOperationsService::getSyncSize, Collectors.toList()))
                .values().stream()
                .filter(list -> list.size() > 1)
                .collect(Collectors.toList());
    }

    private List<List<FileSync>> getFileSyncsGroupedByMD5(List<List<FileSync>> fileSyncs) {
        return fileSyncs.stream()
                .flatMap(this::groupByMD5)
                .filter(list -> list.size() > 1)
                .collect(Collectors.toList());
    }

    private Stream<List<FileSync>> groupByMD5(List<FileSync> fileSyncsEqualsSize) {
        return fileSyncsEqualsSize.stream()
                .collect(Collectors.groupingBy(directOperationsService::getMD5, Collectors.toList()))
                .values().stream();
    }
}
