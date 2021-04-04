package com.kolosov.synchronizer.service.directOperations.transferStrategy;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.service.lowLevel.pc.PcWorker;
import com.kolosov.synchronizer.service.lowLevel.phone.PhoneWorker;

import java.io.IOException;

import static com.kolosov.synchronizer.enums.Location.PHONE;

public class FileFromPcToPhoneStrategy extends AbstractTransferFileStrategy {

    public FileFromPcToPhoneStrategy(FileSync fileSync, PcWorker pcWorker, PhoneWorker phoneWorker) {
        super(pcWorker, phoneWorker, fileSync);
    }

    @Override
    public void transfer() {
        try {
            transferFileSync(pcWorker, phoneWorker, PHONE);
        } catch (IOException exception) {
            throw new RuntimeException(String.format("Problem with transferring file %s to phone", fileSync.relativePath), exception);
        }
        fileSync.existOnPhone = true;
    }

}
