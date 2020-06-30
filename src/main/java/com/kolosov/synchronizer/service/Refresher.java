package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.HistorySync;
import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.enums.ProposedAction;
import com.kolosov.synchronizer.repository.HistorySyncRepository;
import com.kolosov.synchronizer.repository.RootFolderSyncRepository;
import com.kolosov.synchronizer.repository.SyncRepository;
import com.kolosov.synchronizer.service.directOperations.DirectOperationsService;
import com.kolosov.synchronizer.utils.SyncUtils;
import com.kolosov.synchronizer.validators.action.ActionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kolosov.synchronizer.enums.ProposedAction.NOTHING;

@Service
@RequiredArgsConstructor
@Slf4j
public class Refresher {

    private final DirectOperationsService directOperations;
    private final SyncRepository syncRepository;
    private final RootFolderSyncRepository rootFolderSyncRepository;
    private final HistorySyncRepository historySyncRepository;

    public void refresh() {
        log.info("refresh start");
        directOperations.connect();
        List<RootFolderSync> mergedList = directOperations.getMergedList();
        directOperations.disconnect();
        createHistorySyncs(mergedList);
        syncRepository.deleteAll();
        syncRepository.saveAll(mergedList);
        log.info("refresh done");
    }

    private void createHistorySyncs(List<? extends FolderSync> mergedList) {
        Map<String, Sync> oldFlatSyncs = SyncUtils.getFlatSyncs(rootFolderSyncRepository.findAll()).stream()
                .collect(Collectors.toMap(sync -> sync.relativePath, Function.identity()));
        List<HistorySync> oldHistorySyncs = historySyncRepository.findAll();
        SyncUtils.getFlatSyncs(mergedList).forEach(newSync -> {
            Optional<HistorySync> oldHistorySync = getOldHistorySync(oldHistorySyncs, newSync);
            ProposedAction action = ActionValidator.validate(newSync, oldHistorySync, oldFlatSyncs);
            if (action != NOTHING) {
                newSync.setHistorySync(new HistorySync(newSync, action));
            }
        });
    }

    private static Optional<HistorySync> getOldHistorySync(List<HistorySync> oldHistorySyncs, Sync newSync) {
        return oldHistorySyncs.stream()
                .filter(historySync -> newSync.equals(historySync.getSync())).findFirst();
    }

    public void disconnect() {
        directOperations.disconnect();
        log.info("disconnected");
    }
}
