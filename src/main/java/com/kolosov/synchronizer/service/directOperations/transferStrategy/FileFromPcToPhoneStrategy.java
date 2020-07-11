package com.kolosov.synchronizer.service.directOperations.transferStrategy;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.service.lowLevel.pc.PcWorker;
import com.kolosov.synchronizer.service.lowLevel.phone.PhoneWorker;

import static com.kolosov.synchronizer.enums.Location.PHONE;

public class FileFromPcToPhoneStrategy extends AbstractTransferFileStrategy {

    public FileFromPcToPhoneStrategy(FileSync fileSync, PcWorker pcWorker, PhoneWorker phoneWorker) {
        super(pcWorker, phoneWorker, fileSync);
    }

    @Override
    public void transfer() {
        transferFileSync(pcWorker, phoneWorker, PHONE);
        fileSync.existOnPhone = true;
    }

}
