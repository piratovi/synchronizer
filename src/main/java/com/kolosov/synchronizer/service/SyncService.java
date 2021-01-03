package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.HistorySync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.dto.ExtensionStat;
import com.kolosov.synchronizer.repository.HistorySyncRepository;
import com.kolosov.synchronizer.repository.SyncRepository;
import com.kolosov.synchronizer.service.refresher.Refresher;
import com.kolosov.synchronizer.service.transporter.Transporter;
import com.kolosov.synchronizer.utils.SyncUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncService {

    private final SyncRepository syncRepository;
    private final TreeService treeService;
    private final HistorySyncRepository historySyncRepository;
    private final Remover remover;
    private final Transporter transporter;
    private final SynchronizedScout synchronizedScout;
    private final Refresher refresher;
    private final DuplicateScout duplicateScout;
    private final ExtensionService extensionService;
    public final AutoSynchronizationService autoSynchronizationService;

    public List<FolderSync> getEmptyFolders() {
        TreeSync tree = treeService.getTreeSync();
        return SyncUtils.getFoldersWithoutNestedFiles(tree, false);
    }

    public List<ExtensionStat> getExtensionStats() {
        return extensionService.getExtensionStats();
    }

    public void deleteAll() {
        syncRepository.deleteAll();
    }

    public List<HistorySync> getHistorySyncs() {
        return historySyncRepository.findAll();
    }

    public void delete(List<Integer> ids) {
        remover.remove(ids);
    }

    public void transfer(List<Integer> ids) {
        transporter.transfer(ids);
    }

    public List<Sync> getNotSynchronizedSyncs() {
        return synchronizedScout.getNotSynchronizedSyncs().list;
    }

    public void refresh() {
        refresher.refresh();
    }

    public void disconnect() {
        refresher.disconnect();
    }

    public void connectPhone() {
        refresher.connectPhone();
    }

    public List<List<FileSync>> findDuplicateSyncs() {
        return duplicateScout.findDuplicateSyncs();
    }

    public void deleteDuplicateSyncs() {
        duplicateScout.deleteDuplicateSyncs();
    }

    public TreeSync getTreeSync() {
        return treeService.getTreeSync();
    }

    public void autoSynchronization() {
        autoSynchronizationService.autoSynchronization();
    }

}

