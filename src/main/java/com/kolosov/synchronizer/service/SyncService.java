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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kolosov.synchronizer.enums.ProposedAction.REMOVE;
import static com.kolosov.synchronizer.enums.ProposedAction.TRANSFER;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncService {

    private final SyncRepository syncRepository;
    private final RootFolderSyncRepository rootFolderSyncRepository;
    private final HistorySyncRepository historySyncRepository;
    private final Remover remover;
    private final Transporter transporter;
    private final SynchronizedScout synchronizedScout;
    private final Refresher refresher;
    private final DuplicateScout duplicateScout;

    public List<FolderSync> getEmptyFolders() {
        return SyncUtils.getEmptyFolders(rootFolderSyncRepository.findAll());
    }

    public List<ExtensionStat> getExtensionStats() {
        return synchronizedScout.getExtensionStats();
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
        return synchronizedScout.findNotSynchronizedSyncs();
    }

    public void refresh() {
        refresher.refresh();
    }

    public void disconnect() {
        refresher.disconnect();
    }

    public void autoSynchronization() {
        log.info("Auto Synchronizing start");
        synchronizedScout.findNotSynchronizedSyncs().stream()
                .flatMap(FolderSync::getNestedSyncs)
                .forEach(this::applyProposedAction);
        log.info("Auto Synchronizing end");
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

    public void connectPhone() {
        refresher.connectPhone();
    }

    public List<List<Sync>> findDuplicateSyncs() {
        return duplicateScout.findDuplicateSyncs();
    }

    public void deleteDuplicateSyncs() {
        List<List<Sync>> duplicateSyncs = duplicateScout.findDuplicateSyncs();
        duplicateSyncs.stream()
                .peek(list -> list.remove(0))
                .flatMap(Collection::stream)
                .forEach(remover::remove);
    }

}

