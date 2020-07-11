package com.kolosov.synchronizer.service.directOperations.transferStrategy;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.service.lowLevel.pc.PcWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.kolosov.synchronizer.enums.Location.PC;

@RequiredArgsConstructor
@Slf4j
public class FolderFromPhoneToPcStrategy implements TransferStrategy {

    private final FolderSync folderSync;
    private final PcWorker pcWorker;

    @Override
    public void transfer() {
        pcWorker.createFolder(folderSync);
        folderSync.existOnPC = true;
        log.info(folderSync.relativePath + " transferred to " + PC);
    }
}
