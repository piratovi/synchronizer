package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.exceptions.ExceptionSupplier;
import com.kolosov.synchronizer.repository.SyncRepository;
import com.kolosov.synchronizer.repository.TreeSyncRepository;
import com.kolosov.synchronizer.service.directOperations.DirectOperationsService;
import com.kolosov.synchronizer.utils.SyncUtils;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class Remover {

    private final SyncRepository syncRepository;
    private final DirectOperationsService directOperations;
    private final TreeSyncRepository treeSyncRepository;

    @Synchronized
    public void remove(List<Integer> ids) {
        log.info("Deleting start");
        List<Sync> syncsToDelete = ids.stream()
                .map(id -> syncRepository.findById(id).orElseThrow(ExceptionSupplier.syncNotFound(id)))
                .flatMap(Sync::getNestedSyncs)
                .filter(Sync::isNotSynchronized)
                .filter(Sync::isFile)
                .collect(Collectors.toList());
//        directOperations.connectPhone();
        syncsToDelete.forEach(this::remove);
        removeEmptyFolders();
//        directOperations.disconnectPhone();
        log.info("Deleting end");
    }

    private void removeEmptyFolders() {
        List<FolderSync> emptyFolders = SyncUtils.getEmptyFolders(treeSyncRepository.findTree());
        while (!emptyFolders.isEmpty()) {
            emptyFolders.forEach(this::remove);
            emptyFolders = SyncUtils.getEmptyFolders(treeSyncRepository.findTree());
        }
    }

    public void remove(Sync sync) {
        directOperations.delete(sync);
        sync.removeFromParent();
        syncRepository.delete(sync);
    }

}
