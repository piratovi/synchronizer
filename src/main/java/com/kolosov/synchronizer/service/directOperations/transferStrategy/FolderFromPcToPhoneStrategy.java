package com.kolosov.synchronizer.service.directOperations.transferStrategy;

import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.service.lowLevel.phone.PhoneWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.kolosov.synchronizer.enums.Location.PHONE;

@RequiredArgsConstructor
@Slf4j
public class FolderFromPcToPhoneStrategy implements TransferStrategy {

    private final Sync sync;
    private final PhoneWorker phoneWorker;

    @Override
    public void transfer() {
        phoneWorker.createFolder(sync.asFolder());
        sync.existOnPhone = true;
        log.info(sync.relativePath + " transferred to " + PHONE);
    }
}
