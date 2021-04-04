package com.kolosov.synchronizer.service.directOperations.transferStrategy;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.service.lowLevel.phone.PhoneWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.kolosov.synchronizer.enums.Location.PHONE;

@RequiredArgsConstructor
@Slf4j
public class FolderFromPcToPhoneStrategy implements TransferStrategy {

    private final FolderSync folderSync;
    private final PhoneWorker phoneWorker;

    @Override
    public void transfer() {
        try {
            phoneWorker.createFolder(folderSync);
        } catch (IOException exception) {
            throw new RuntimeException(String.format("Problem with creating folder %s on phone", folderSync.relativePath), exception);
        }
        folderSync.existOnPhone = true;
        log.info(String.format("Transferred to %5s : %s.", PHONE, folderSync.relativePath));
    }
}
