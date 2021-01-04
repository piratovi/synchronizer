package com.kolosov.synchronizer.service.refresher;

import com.kolosov.synchronizer.domain.HistorySync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.enums.ProposedAction;
import com.kolosov.synchronizer.repository.SyncRepository;
import com.kolosov.synchronizer.service.TreeService;
import com.kolosov.synchronizer.service.directOperations.DirectOperationsService;
import com.kolosov.synchronizer.service.refresher.validator.ActionValidator;
import com.kolosov.synchronizer.utils.SyncUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.kolosov.synchronizer.enums.ProposedAction.NOTHING;

@Service
@RequiredArgsConstructor
@Slf4j
public class Refresher {

    private final DirectOperationsService directOperationsService;
    private final SyncRepository syncRepository;
    private final TreeService treeService;

    public void refresh() {
        log.info("refresh start");
        TreeSync newTreeSync = directOperationsService.getNewTreeSync();
        createHistorySyncs(newTreeSync);
        syncRepository.deleteAll();
        //TODO проверить
//        syncRepository.save(newTreeSync);
        treeService.save(newTreeSync);
        log.info("refresh done");
    }

    private void createHistorySyncs(TreeSync newTreeSync) {
        Optional<TreeSync> oldTreeSyncOpt = treeService.findTreeSync();
        newTreeSync.getNestedSyncs().forEach(newSync -> {
            Optional<Sync> oldSyncOpt = oldTreeSyncOpt.flatMap(oldTreeSync -> SyncUtils.findSync(oldTreeSync, newSync));
            ProposedAction action = ActionValidator.validate(newSync, oldSyncOpt);
            if (action != NOTHING) {
                newSync.setHistorySync(new HistorySync(newSync, action));
            }
        });
    }

    public void disconnect() {
        directOperationsService.disconnectPhone();
    }

    public void connectPhone() {
        directOperationsService.connectPhone();
    }
}
