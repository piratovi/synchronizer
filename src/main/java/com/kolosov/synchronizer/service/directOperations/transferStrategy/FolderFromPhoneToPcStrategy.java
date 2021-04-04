package com.kolosov.synchronizer.service.directOperations.transferStrategy;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.service.lowLevel.pc.PcWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.kolosov.synchronizer.enums.Location.PC;

@RequiredArgsConstructor
@Slf4j
public class FolderFromPhoneToPcStrategy implements TransferStrategy {

    private final FolderSync folderSync;
    private final PcWorker pcWorker;

    @Override
    public void transfer() {
        try {
            pcWorker.createFolder(folderSync);
        } catch (IOException exception) {
            throw new RuntimeException(String.format("Problem with creating folder %s on pc", folderSync.relativePath), exception);
        }
        folderSync.existOnPc = true;
        log.info(String.format("Transferred to %5s : %s", PC, folderSync.relativePath));
    }
}
