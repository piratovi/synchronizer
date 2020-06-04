package com.kolosov.synchronizer.service.directOperations.transferStrategy;

import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.service.lowLevel.pc.PcWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.kolosov.synchronizer.enums.Location.PC;

@RequiredArgsConstructor
@Slf4j
public class FolderFromPhoneToPcStrategy implements TransferStrategy {

    private final Sync sync;
    private final PcWorker pcWorker;

    @Override
    public void transfer() {
        pcWorker.createFolder(sync.asFolder());
        sync.existOnPC = true;
        log.info(sync.relativePath + " transferred to " + PC);
    }
}
