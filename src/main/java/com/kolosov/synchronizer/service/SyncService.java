package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.HistorySync;
import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.dto.ExtensionStat;
import com.kolosov.synchronizer.repository.HistorySyncRepository;
import com.kolosov.synchronizer.repository.RootFolderSyncRepository;
import com.kolosov.synchronizer.repository.SyncRepository;
import com.kolosov.synchronizer.service.transporter.Transporter;
import com.kolosov.synchronizer.utils.SyncUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.kolosov.synchronizer.enums.ProposedAction.REMOVE;
import static com.kolosov.synchronizer.enums.ProposedAction.TRANSFER;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final SyncRepository syncRepository;
    private final RootFolderSyncRepository rootFolderSyncRepository;
    private final HistorySyncRepository historySyncRepository;
    private final Remover remover;
    private final Transporter transporter;
    private final Scout scout;
    private final Refresher refresher;

    public List<FolderSync> getEmptyFolders() {
        return SyncUtils.getEmptyFolders(rootFolderSyncRepository.findAll());
    }

    public List<ExtensionStat> getExtensionStats() {
        return scout.getExtensionStats();
    }

    public void clear() {
        syncRepository.deleteAll();
        historySyncRepository.deleteAll();
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

    public List<RootFolderSync> getNotSynchronizedSyncs() {
        return scout.findNotSynchronizedSyncs();
    }

    public void refresh() {
        refresher.refresh();
    }

    public void disconnect() {
        refresher.disconnect();
    }

    public void auto() {
        scout.findNotSynchronizedSyncs().stream()
                .map(FolderSync::getNestedSyncs)
                .flatMap(Collection::stream)
                .forEach(this::applyProposedAction);
    }

    private void applyProposedAction(Sync sync) {
        Optional.ofNullable(sync.getHistorySync())
                .ifPresent(historySync -> {
                    if (TRANSFER.equals(historySync.action)) {
                        transporter.transfer(sync);
                    } else if (REMOVE.equals(historySync.action)) {
                        remover.remove(sync);
                    }
                });
    }
}

