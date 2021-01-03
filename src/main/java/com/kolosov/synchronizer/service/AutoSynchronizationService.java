package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.HistorySync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.repository.HistorySyncRepository;
import com.kolosov.synchronizer.service.refresher.Refresher;
import com.kolosov.synchronizer.service.transporter.Transporter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.kolosov.synchronizer.enums.ProposedAction.REMOVE;
import static com.kolosov.synchronizer.enums.ProposedAction.TRANSFER;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoSynchronizationService {

    private final HistorySyncRepository historySyncRepository;
    private final Transporter transporter;
    private final Remover remover;
    private final Refresher refresher;

    public void autoSynchronization() {
        log.info("Auto Synchronizing start");
        refresher.refresh();
        historySyncRepository.findAll()
                .forEach(this::applyProposedAction);
        log.info("Auto Synchronizing end");
    }

    private void applyProposedAction(HistorySync historySync) {
        Sync sync = historySync.sync;
        if (TRANSFER.equals(historySync.action)) {
            transporter.transfer(sync);
        } else if (REMOVE.equals(historySync.action)) {
            remover.remove(sync);
        }
        sync.setHistorySync(null);
        historySyncRepository.delete(historySync);
    }

}