package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.HistorySync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.enums.ProposedAction;
import com.kolosov.synchronizer.repository.HistorySyncRepository;
import com.kolosov.synchronizer.repository.SyncRepository;
import com.kolosov.synchronizer.service.directOperations.DirectOperationsService;
import com.kolosov.synchronizer.validators.action.ActionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
    private final TreeService treeService;
    private final HistorySyncRepository historySyncRepository;

    public void refresh() {
        log.info("refresh start");
        TreeSync newTreeSync = directOperations.getNewTreeSync();
        createHistorySyncs(newTreeSync);
        syncRepository.deleteAll();
        treeService.save(newTreeSync);
        log.info("refresh done");
    }

    private void createHistorySyncs(TreeSync newTreeSync) {
        TreeSync oldTreeSync = treeService.getTreeSync();
        Map<String, Sync> mappedOldSyncs;
        if (oldTreeSync != null) {
            mappedOldSyncs = oldTreeSync
                    .getNestedSyncs()
                    .collect(Collectors.toMap(
                            sync -> sync.relativePath,
                            Function.identity()));
        } else {
            mappedOldSyncs = Collections.emptyMap();
        }
        List<HistorySync> oldHistorySyncs = historySyncRepository.findAll();
        newTreeSync.getNestedSyncs().forEach(newSync -> {
            Optional<HistorySync> oldHistorySync = getOldHistorySync(oldHistorySyncs, newSync);
            ProposedAction action = ActionValidator.validate(newSync, oldHistorySync, mappedOldSyncs);
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
        directOperations.disconnectPhone();
    }

    public void connectPhone() {
        directOperations.connectPhone();
    }
}
