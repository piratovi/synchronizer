package com.kolosov.synchronizer.service;


import com.kolosov.synchronizer.domain.Sync;
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
    private final SynchronizedScout synchronizedScout;

    public List<List<Sync>> findDuplicateSyncs() {
        if (synchronizedScout.isTreeSyncNotSynchronized()) {
            throw new RuntimeException("Need synchronization!");
        }
        return treeService.getTreeSync()
                .getNestedSyncs()
                .filter(Sync::isFile)
                .collect(Collectors.groupingBy(directOperationsService::getSyncSize, Collectors.toList()))
                .values().stream()
                .filter(list -> list.size() > 1)
                .flatMap(this::getMD5Equals)
                .collect(Collectors.toList());
    }

    private Stream<List<Sync>> getMD5Equals(List<Sync> listEqualsSize) {
        return listEqualsSize.stream()
                .collect(Collectors.groupingBy(directOperationsService::getMD5, Collectors.toList()))
                .values().stream()
                .filter(listEqualsMD5 -> listEqualsMD5.size() > 1);
    }
}
