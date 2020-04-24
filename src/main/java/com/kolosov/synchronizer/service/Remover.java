package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.exceptions.ExceptionSupplier;
import com.kolosov.synchronizer.repository.RootFolderSyncRepository;
import com.kolosov.synchronizer.repository.SyncRepository;
import com.kolosov.synchronizer.utils.SyncUtils;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class Remover {

    private final SyncRepository syncRepository;
    private final DirectOperationsService directOperations;
    private final RootFolderSyncRepository rootFolderSyncRepository;

    @Synchronized
    public void delete(List<Integer> ids) {
        log.info("Deleting start");
        List<Sync> syncsToDelete = ids.stream()
                .map(id -> syncRepository.findById(id).orElseThrow(ExceptionSupplier.syncNotFound(id)))
                .map(SyncUtils::getFlatSyncs)
                .flatMap(Collection::stream)
                .filter(Sync::isNotSynchronized)
                .filter(Sync::isFile)
                .collect(Collectors.toList());
        syncsToDelete.forEach(this::delete);
        removeEmptyFoldersInLowLevelAndRepo();
        log.info("Deleting end");
    }

    private void removeEmptyFoldersInLowLevelAndRepo() {
        List<FolderSync> folders = SyncUtils.getEmptyFolders(rootFolderSyncRepository.findAll());
        while (!folders.isEmpty()) {
            folders.forEach(this::delete);
            folders = SyncUtils.getEmptyFolders(rootFolderSyncRepository.findAll());
        }
    }

    private void delete(Sync sync) {
        directOperations.delete(sync);
        sync.removeFromParent();
        syncRepository.delete(sync);
        log.info(sync.relativePath + " deleted");
    }

}
