package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.dto.ExtensionStat;
import com.kolosov.synchronizer.domain.HistorySync;
import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.enums.ProposedAction;
import com.kolosov.synchronizer.exceptions.ExceptionSupplier;
import com.kolosov.synchronizer.repository.HistorySyncRepository;
import com.kolosov.synchronizer.repository.RootFolderSyncRepository;
import com.kolosov.synchronizer.repository.SyncRepository;
import com.kolosov.synchronizer.utils.SyncUtils;
import com.kolosov.synchronizer.validators.action.ActionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kolosov.synchronizer.enums.ProposedAction.NOTHING;


@Service
@RequiredArgsConstructor
@Slf4j
public class SyncService {

    private final DirectOperationsService directOperations;
    private final SyncRepository syncRepository;
    private final RootFolderSyncRepository rootFolderSyncRepository;
    private final HistorySyncRepository historySyncRepository;

    public static Optional<HistorySync> getOldHistorySync(List<HistorySync> oldHistorySyncs, Sync newSync) {
        return oldHistorySyncs.stream()
                .filter(historySync -> newSync.equals(historySync.getSync())).findFirst();
    }

    public void delete(Integer id) {
        Sync sync = syncRepository.findById(id).orElseThrow(ExceptionSupplier.syncNotFound(id));
        delete(sync);
    }

    private void delete(Sync syncToDelete) {
        directOperations.delete(syncToDelete);
        syncRepository.delete(syncToDelete);
    }

    public List<FolderSync> getEmptyFolders() {
        return SyncUtils.getEmptyFolders(rootFolderSyncRepository.findAll());
    }

    public void deleteEmptyFolders() {
        deleteSyncs(getEmptyFolders());
    }

    private void deleteSyncs(List<? extends Sync> syncs) {
        for (Sync sync : syncs) {
            delete(sync);
        }
    }

    public void refresh() {
        log.info("refresh start");
        List<FolderSync> mergedList = directOperations.getMergedList();
        List<HistorySync> newHistorySyncs = createHistorySyncs(mergedList);
        syncRepository.deleteAll();
        syncRepository.saveAll(mergedList);
        historySyncRepository.deleteAll();
        historySyncRepository.saveAll(newHistorySyncs);
        log.info("refresh done");
    }

    private List<HistorySync> createHistorySyncs(List<FolderSync> mergedList) {
        Map<String, Sync> oldFlatSyncs = SyncUtils.getFlatSyncs(rootFolderSyncRepository.findAll()).stream()
                .collect(Collectors.toMap(sync -> sync.relativePath, Function.identity()));
        List<HistorySync> oldHistorySyncs = historySyncRepository.findAll();
        List<HistorySync> newHistorySyncs = new ArrayList<>();
        SyncUtils.getFlatSyncs(mergedList).forEach(newSync -> {

            Optional<HistorySync> oldHistorySync = getOldHistorySync(oldHistorySyncs, newSync);
            ProposedAction action = ActionValidator.validate(newSync, oldHistorySync, oldFlatSyncs);

            if (action != NOTHING) {
                newHistorySyncs.add(new HistorySync(newSync, action));
            }

        });
        return newHistorySyncs;
    }

    private static List<Sync> subtract(List<Sync> list1, List<Sync> list2) {
        List<Sync> diff = new ArrayList<>(list1);
        return diff.stream()
                .filter(fileEntity -> !list2.contains(fileEntity))
                .collect(Collectors.toList());
    }

    public void transferSync(Integer id) {
        Sync sync = syncRepository.findById(id).orElseThrow();
        if (sync.existOnPC) {
            directOperations.copyFileFromPcToPhone(sync);
            sync.existOnPhone = true;
        } else {
            directOperations.copyFileFromPhoneToPc(sync);
            sync.existOnPC = true;
        }
        syncRepository.save(sync);
    }

    public List<ExtensionStat> getExtensionStats() {
        List<Sync> flatSyncs = SyncUtils.getFlatSyncs(rootFolderSyncRepository.findAll());
        return flatSyncs.stream()
                .filter(sync -> sync instanceof FileSync)
                .map(sync -> (FileSync) sync)
                .collect(Collectors.groupingBy(sync -> sync.ext, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> new ExtensionStat(entry.getKey(), entry.getValue().size(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public void clear() {
        syncRepository.deleteAll();
        historySyncRepository.deleteAll();
    }

    public List<RootFolderSync> getNotSynchronizedSyncs() {
        List<RootFolderSync> rootFolders = rootFolderSyncRepository.findAll();
        List<Sync> flatSyncs = SyncUtils.getFlatSyncs(rootFolders);
        removeSynchronizedFiles(flatSyncs);
        removeEmptyFoldersInList(rootFolders);
        return rootFolders;
    }

    private void removeSynchronizedFiles(List<Sync> flatSyncs) {
        flatSyncs.stream()
                .filter(Sync::isFile)
                .map(Sync::asFile)
                .filter(FileSync::isSynchronized)
                .forEach(FileSync::removeFromParent);
    }

    private void removeEmptyFoldersInList(List<RootFolderSync> rootFolders) {
        List<FolderSync> folders = getEmptyFolders(SyncUtils.getFlatSyncs(rootFolders));
        while (!folders.isEmpty()) {
            removeEmptyFoldersInList(rootFolders, folders);
            folders = getEmptyFolders(SyncUtils.getFlatSyncs(rootFolders));
        }
    }

    private void removeEmptyFoldersInLowLevelAndRepo() {
        List<FolderSync> folders = getEmptyFolders(SyncUtils.getFlatSyncs(rootFolderSyncRepository.findAll()));
        while (!folders.isEmpty()) {
            folders.forEach(this::delete);
            folders = getEmptyFolders(SyncUtils.getFlatSyncs(rootFolderSyncRepository.findAll()));
        }
    }

    public static List<FolderSync> getEmptyFolders(List<? extends Sync> syncs) {
        return syncs.stream()
                .filter(Sync::isFolder)
                .map(Sync::asFolder)
                .filter(FolderSync::isEmpty)
                .collect(Collectors.toList());
    }

    private void removeEmptyFoldersInList(List<RootFolderSync> rootFolders, List<FolderSync> folders) {
        folders.forEach(folderSync -> {
                    if (folderSync.isRootFolder()) {
                        rootFolders.remove(folderSync);
                    } else {
                        folderSync.removeFromParent();
                    }
                });
    }

    public List<HistorySync> getHistorySyncs() {
        return historySyncRepository.findAll();
    }

    public void delete(List<Integer> ids) {
        List<Sync> flatSyncs = ids.stream()
                .map(id -> syncRepository.findById(id).orElseThrow(ExceptionSupplier.syncNotFound(id)))
                .map(SyncUtils::getFlatSyncs)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        for (int i = flatSyncs.size() - 1; i >= 0 ; i--) {
            delete(flatSyncs.get(i));
        }
        removeEmptyFoldersInLowLevelAndRepo();
    }
}

