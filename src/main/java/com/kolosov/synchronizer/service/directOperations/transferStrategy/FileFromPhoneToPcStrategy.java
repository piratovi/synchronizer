package com.kolosov.synchronizer.service.directOperations.transferStrategy;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.service.lowLevel.pc.PcWorker;
import com.kolosov.synchronizer.service.lowLevel.phone.PhoneWorker;

import static com.kolosov.synchronizer.enums.Location.PC;

public class FileFromPhoneToPcStrategy extends AbstractTransferFileStrategy {

    public FileFromPhoneToPcStrategy(FileSync fileSync, PcWorker pcWorker, PhoneWorker phoneWorker) {
        super(pcWorker, phoneWorker, fileSync);
    }

    @Override
    public void transfer() {
        transferFileSync(phoneWorker, pcWorker, PC);
        fileSync.existOnPC = true;
    }

}
