package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.dto.ExtensionStat;
import com.kolosov.synchronizer.domain.HistorySync;
import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.enums.ProposedAction;
import com.kolosov.synchronizer.exceptions.ExceptionSupplier;
import com.kolosov.synchronizer.repository.HistorySyncRepository;
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
    private final HistorySyncRepository historySyncRepository;

    public static Optional<HistorySync> getOldHistorySync(List<HistorySync> oldHistorySyncs, AbstractSync newSync) {
        return oldHistorySyncs.stream()
                .filter(historySync -> newSync.equals(historySync.getSync())).findFirst();
    }

    public void deleteById(Integer id) {
        AbstractSync sync = syncRepository.findById(id).orElseThrow(ExceptionSupplier.syncNotFound(id));
        deleteSync(sync);
    }

    private void deleteSync(AbstractSync syncToDelete) {
        directOperations.deleteFile(syncToDelete);
        syncRepository.delete(syncToDelete);
    }

    public List<FolderSync> getEmptyFolders() {
        return SyncUtils.getEmptyFolders(syncRepository.findAllByParentNull());
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
        List<FolderSync> mergedList = directOperations.getMergedList();
        List<HistorySync> newHistorySyncs = createHistorySyncs(mergedList);
        syncRepository.deleteAll();
        syncRepository.saveAll(mergedList);
        historySyncRepository.deleteAll();
        historySyncRepository.saveAll(newHistorySyncs);
        log.info("refresh done");
    }

    private List<HistorySync> createHistorySyncs(List<FolderSync> mergedList) {
        //TODO посмотреть че там с мерджконфликтом

        Map<String, AbstractSync> oldFlatSyncs = SyncUtils.getFlatSyncs(syncRepository.findAllByParentNull()).stream()
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

    private static List<AbstractSync> subtract(List<AbstractSync> list1, List<AbstractSync> list2) {
        List<AbstractSync> diff = new ArrayList<>(list1);
        return diff.stream()
                .filter(fileEntity -> !list2.contains(fileEntity))
                .collect(Collectors.toList());
    }

    public void transferSync(Integer id) {
        AbstractSync sync = syncRepository.findById(id).orElseThrow();
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
        List<AbstractSync> flatSyncs = SyncUtils.getFlatSyncs(syncRepository.findAllByParentNull());
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

    public List<FolderSync> getNotSynchronizedSyncs() {
        List<FolderSync> rootFolders = syncRepository.findAllByParentNull();
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

    public List<HistorySync> getHistorySyncs() {
        return historySyncRepository.findAll();
    }
}
