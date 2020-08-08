package com.kolosov.synchronizer.service.directOperations.transferStrategy;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.service.lowLevel.phone.PhoneWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.kolosov.synchronizer.enums.Location.PHONE;

@RequiredArgsConstructor
@Slf4j
public class FolderFromPcToPhoneStrategy implements TransferStrategy {

    private final FolderSync folderSync;
    private final PhoneWorker phoneWorker;

    @Override
    public void transfer() {
        phoneWorker.createFolder(folderSync);
        folderSync.existOnPhone = true;
        log.info(String.format("Transferred to %5s : %s.", PHONE, folderSync.relativePath));
    }
}
