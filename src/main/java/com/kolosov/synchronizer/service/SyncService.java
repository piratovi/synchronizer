package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.ExtensionStat;
import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.exceptions.SyncNotFoundException;
import com.kolosov.synchronizer.repository.SyncRepository;
import com.kolosov.synchronizer.repository.TreeSyncRepository;
import com.kolosov.synchronizer.utils.SyncUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class SyncService {

    private final DirectOperationsService directOperations;
    private final TreeSyncRepository treeSyncRepository;
    private final SyncRepository syncRepository;

    public void deleteById(Long id) {
        //TODO Создать свой эксепшен?
        Optional<AbstractSync> syncOpt = syncRepository.findById(id);
        if (syncOpt.isPresent()) {
            deleteSync(syncOpt.get());
        } else {
            throw new SyncNotFoundException("Sync not found");
        }
    }

    private void deleteSync(AbstractSync syncToDelete) {
        directOperations.deleteFile(syncToDelete);
        cleanTree(syncToDelete);
    }

    private void cleanTree(AbstractSync syncToDelete) {
        FolderSync parentSync = syncToDelete.parent;
        if (parentSync != null) {
            parentSync.list.remove(syncToDelete);
            syncRepository.save(parentSync);
        } else {
            TreeSync treeSync = getTreeSync();
            boolean remove = treeSync.folderSyncs.remove(syncToDelete);
            if (!remove) {
                throw new RuntimeException("Error With deleting");
            }
            treeSyncRepository.save(treeSync);
            syncRepository.delete(syncToDelete);
        }
    }

    public List<FolderSync> getEmptyFolders() {
        return SyncUtils.getEmptyFolders(getTreeSync());
    }

    public void deleteEmptyFolders() {
        deleteSyncs(getEmptyFolders());
    }

    private void deleteSyncs(List<? extends AbstractSync> syncs) {
        for (AbstractSync sync : syncs) {
            deleteSync(sync);
        }
    }

    public void refresh() {
        log.info("refresh start");
        treeSyncRepository.deleteAll();
        syncRepository.deleteAll();
        createTreeSync();
        log.info("refresh done");
    }

    private void createTreeSync() {
        List<FolderSync> mergedList = directOperations.getMergedList();
        TreeSync treeSync = new TreeSync(mergedList);
        treeSyncRepository.save(treeSync);
    }

    private static List<AbstractSync> subtract(List<AbstractSync> list1, List<AbstractSync> list2) {
        List<AbstractSync> diff = new ArrayList<>(list1);
        return diff.stream()
                .filter(fileEntity -> !list2.contains(fileEntity))
                .collect(Collectors.toList());
    }

    public TreeSync getTreeSync() {
        return treeSyncRepository.findAll().get(0);
    }

    public void transferSync(Long id) {
        AbstractSync sync = syncRepository.findById(id).orElseThrow();
        if (sync.existOnPC && sync.existOnPhone || !sync.existOnPC && !sync.existOnPhone) {
            throw new RuntimeException();
        }
        if (sync.existOnPC) {
            directOperations.copyFileFromPcToPhone(sync);
        } else {
            directOperations.copyFileFromPhoneToPc(sync);
        }
    }

    public List<ExtensionStat> getExtensionStats() {
        List<AbstractSync> flatSyncs = SyncUtils.getFlatSyncs(getTreeSync().folderSyncs);
        return flatSyncs.stream()
                .filter(sync -> sync instanceof FileSync)
                .map(sync -> (FileSync) sync)
                .collect(Collectors.groupingBy(sync -> sync.ext, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> new ExtensionStat(entry.getKey(), entry.getValue().size(), entry.getValue()))
                .collect(Collectors.toList());
    }
}

