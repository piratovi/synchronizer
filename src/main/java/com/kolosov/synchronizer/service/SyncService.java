package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.dto.ExtensionStat;
import com.kolosov.synchronizer.domain.HistorySync;
import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.enums.ProposedAction;
import com.kolosov.synchronizer.exceptions.SyncNotFoundException;
import com.kolosov.synchronizer.repository.HistorySyncRepository;
import com.kolosov.synchronizer.repository.SyncRepository;
import com.kolosov.synchronizer.repository.TreeSyncRepository;
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
    private final TreeSyncRepository treeSyncRepository;
    private final SyncRepository syncRepository;
    private final HistorySyncRepository historySyncRepository;

    public static Optional<HistorySync> getOldHistorySync(List<HistorySync> oldHistorySyncs, AbstractSync newSync) {
        return oldHistorySyncs.stream()
                .filter(historySync -> newSync.equals(historySync.getSync())).findFirst();
    }

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
            syncRepository.delete(syncToDelete);
            treeSyncRepository.save(treeSync);
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
        TreeSync treeSyncOld = getTreeSync();
        List<FolderSync> mergedList = directOperations.getMergedList();
        TreeSync treeSyncNew = new TreeSync(mergedList);
        createHistorySyncs(treeSyncOld, treeSyncNew);
        syncRepository.deleteAll();
        historySyncRepository.deleteAll();
        treeSyncRepository.deleteAll();
        treeSyncRepository.save(treeSyncNew);
        log.info("refresh done");
    }

    private void createHistorySyncs(TreeSync oldTreeSync, TreeSync newTreeSync) {
        //TODO посмотреть че там с мерджконфликтом

        Map<String, AbstractSync> oldFlatSyncs = SyncUtils.getFlatSyncs(oldTreeSync.folderSyncs).stream()
                .collect(Collectors.toMap(sync -> sync.relativePath, Function.identity(), (abstractSync1, abstractSync2) -> abstractSync1));
        List<HistorySync> oldHistorySyncs = oldTreeSync.getHistorySyncs();

        SyncUtils.getFlatSyncs(newTreeSync.folderSyncs).forEach(newSync -> {

            Optional<HistorySync> oldHistorySync = getOldHistorySync(oldHistorySyncs, newSync);
            ProposedAction action = ActionValidator.validate(newSync, oldHistorySync, oldFlatSyncs);

            if (action != NOTHING) {
                newTreeSync.historySyncs.add(new HistorySync(newSync, action));
            }

        });
    }

    private static List<AbstractSync> subtract(List<AbstractSync> list1, List<AbstractSync> list2) {
        List<AbstractSync> diff = new ArrayList<>(list1);
        return diff.stream()
                .filter(fileEntity -> !list2.contains(fileEntity))
                .collect(Collectors.toList());
    }

    public TreeSync getTreeSync() {
        List<TreeSync> trees = treeSyncRepository.findAll();
        if (!trees.isEmpty()) {
            return trees.get(0);
        }
        return new TreeSync();
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

    public void clear() {
        treeSyncRepository.deleteAll();
    }

    public List<FolderSync> getNotSynchronizedSyncs() {
        TreeSync treeSync = getTreeSync();
        List<FolderSync> rootFolders = treeSync.folderSyncs;
        List<AbstractSync> flatSyncs = SyncUtils.getFlatSyncs(rootFolders);
        removeSynchronizedFiles(flatSyncs);
        removeEmptyFolders(rootFolders);
        return rootFolders;
    }

    private void removeSynchronizedFiles(List<AbstractSync> flatSyncs) {
        flatSyncs.stream()
                .filter(AbstractSync::isFile)
                .map(AbstractSync::asFile)
                .filter(FileSync::isSynchronized)
                .forEach(FileSync::removeFromParent);
    }

    private void removeEmptyFolders(List<FolderSync> rootFolders) {
        List<FolderSync> folders = getOnlyEmptyFolders(SyncUtils.getFlatSyncs(rootFolders));
        while (!folders.isEmpty()) {
            removeEmptyFolders(rootFolders, folders);
            folders = getOnlyEmptyFolders(SyncUtils.getFlatSyncs(rootFolders));
        }
    }

    public static List<FolderSync> getOnlyEmptyFolders(List<? extends AbstractSync> syncs) {
        return syncs.stream()
                .filter(AbstractSync::isFolder)
                .map(AbstractSync::asFolder)
                .filter(FolderSync::isEmpty)
                .collect(Collectors.toList());
    }

    private void removeEmptyFolders(List<FolderSync> rootFolders, List<FolderSync> folders) {
        folders.stream()
                .filter(FolderSync::isEmpty)
                .forEach(folderSync -> {
                    if (folderSync.hasParent()) {
                        folderSync.removeFromParent();
                    } else {
                        rootFolders.remove(folderSync);
                    }
                });
    }
}

