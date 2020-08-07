package com.kolosov.synchronizer.service;


import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.repository.TreeSyncRepository;
import com.kolosov.synchronizer.service.directOperations.DirectOperationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DuplicateScout {

    private final TreeSyncRepository treeSyncRepository;
    private final DirectOperationsService directOperationsService;
    private final SynchronizedScout synchronizedScout;

    public List<List<Sync>> findDuplicateSyncs() {
        if (synchronizedScout.isAllSyncsSynchronized()) {
            throw new RuntimeException("Need synchronization!");
        }
        return treeSyncRepository.findTree()
                .getNestedSyncs()
                .filter(Sync::isFile)
                .collect(Collectors.groupingBy(directOperationsService::getSyncSize, Collectors.toList()))
                .values().stream()
                .filter(list -> list.size() > 1)
                .flatMap(listEqualsSize -> listEqualsSize.stream()
                        .collect(Collectors.groupingBy(directOperationsService::getMD5, Collectors.toList()))
                        .values().stream()
                        .filter(listEqualsMD5 -> listEqualsMD5.size() > 1))
                .filter(directOperationsService::isContentEquals)
                .collect(Collectors.toList());
    }
}
