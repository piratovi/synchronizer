package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.ExtensionStat;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kolosov.synchronizer.enums.ProposedAction.DELETE;
import static com.kolosov.synchronizer.enums.ProposedAction.TRANSFER;


@Service
@RequiredArgsConstructor
@Slf4j
public class SyncService {

    private final DirectOperationsService directOperations;
    private final TreeSyncRepository treeSyncRepository;
    private final SyncRepository syncRepository;
    private final HistorySyncRepository historySyncRepository;

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
        TreeSync treeSyncOld = getTreeSync();
        List<FolderSync> mergedList = directOperations.getMergedList();
        TreeSync treeSyncNew = new TreeSync(mergedList);
        createHistorySyncs(treeSyncOld, treeSyncNew);
        treeSyncRepository.deleteAll();
        syncRepository.deleteAll();
        historySyncRepository.deleteAll();
        treeSyncRepository.save(treeSyncNew);
        log.info("refresh done");
    }

    private void createHistorySyncs(TreeSync oldTreeSync, TreeSync newTreeSync) {
        //TODO посмотреть че там с мерджконфликтом
        Map<String, AbstractSync> oldSyncs = SyncUtils.getFlatSyncs(oldTreeSync.folderSyncs).stream()
                .collect(Collectors.toMap(sync -> sync.relativePath, Function.identity(), (abstractSync1, abstractSync2) -> abstractSync1));

        List<HistorySync> oldHistorySyncs = oldTreeSync.getHistorySyncs();

        SyncUtils.getFlatSyncs(newTreeSync.folderSyncs).forEach(newSync -> {
            if (oldSyncs.containsKey(newSync.relativePath)) {
                AbstractSync oldSync = oldSyncs.get(newSync.relativePath);

                Optional<HistorySync> oldHistorySync = getOldHistorySync(oldHistorySyncs, newSync);

                if (syncTransferred(oldSync) && syncNotTransferred(newSync)) {
                    ProposedAction newAction = DELETE;
                    if (oldHistorySync.isPresent()) {
                        if (oldHistorySync.get().action.equals(newAction)) {
                            newTreeSync.historySyncs.add(new HistorySync(newSync, newAction));
                        }
                    } else {
                        newTreeSync.historySyncs.add(new HistorySync(newSync, newAction));
                    }
                }
            } else {
                newTreeSync.historySyncs.add(new HistorySync(newSync, TRANSFER));
            }
        });
    }

    private Optional<HistorySync> getOldHistorySync(List<HistorySync> oldHistorySyncs, AbstractSync newSync) {
        return oldHistorySyncs.stream()
                .filter(historySync -> historySync.getSync().equals(newSync)).findFirst();
    }

    private boolean syncNotTransferred(AbstractSync newSync) {
        return newSync.existOnPhone != newSync.existOnPC;
    }

    private boolean syncTransferred(AbstractSync oldSync) {
        return oldSync.existOnPhone && oldSync.existOnPC;
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
}

