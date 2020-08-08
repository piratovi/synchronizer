package com.kolosov.synchronizer.service.directOperations;

import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.exceptions.FileNotFoundException;
import com.kolosov.synchronizer.service.directOperations.transferStrategy.FileFromPcToPhoneStrategy;
import com.kolosov.synchronizer.service.directOperations.transferStrategy.FileFromPhoneToPcStrategy;
import com.kolosov.synchronizer.service.directOperations.transferStrategy.FolderFromPcToPhoneStrategy;
import com.kolosov.synchronizer.service.directOperations.transferStrategy.FolderFromPhoneToPcStrategy;
import com.kolosov.synchronizer.service.directOperations.transferStrategy.TransferStrategy;
import com.kolosov.synchronizer.service.lowLevel.pc.PcWorker;
import com.kolosov.synchronizer.service.lowLevel.phone.PhoneWorker;
import com.kolosov.synchronizer.service.transporter.validator.TransferType;
import com.kolosov.synchronizer.utils.MergeSyncsUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kolosov.synchronizer.enums.Location.PC;
import static com.kolosov.synchronizer.enums.Location.PHONE;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectOperationsService {

    private final PhoneWorker phoneWorker;
    private final PcWorker pcWorker;

    public void delete(Sync sync) {
        if (!sync.existOnPhone && !sync.existOnPC) {
            throw new FileNotFoundException("File already deleted " + sync.relativePath);
        }
        if (sync.existOnPhone) {
            phoneWorker.delete(sync);
            log.info(String.format("Deleted from %7s : %s", PHONE, sync.relativePath));
        }
        if (sync.existOnPC) {
            pcWorker.delete(sync);
            log.info(String.format("Deleted from %7s : %s", PC, sync.relativePath));
        }
    }

    public TreeSync getNewTreeSync() {
        TreeSync pcTreeSync = pcWorker.getNewTreeSync();
        TreeSync ftpTreeSync = phoneWorker.getNewTreeSync();
        return MergeSyncsUtils.mergeSyncs(pcTreeSync, ftpTreeSync);
    }

    public void transfer(Sync sync, TransferType transferType) {
        TransferStrategy transferStrategy = getTransferStrategy(sync, transferType);
        transferStrategy.transfer();
    }

    private TransferStrategy getTransferStrategy(Sync sync, TransferType transferType) {
        switch (transferType) {
            case FOLDER_FROM_PC_TO_PHONE:
                return new FolderFromPcToPhoneStrategy(sync.asFolder(), phoneWorker);
            case FILE_FROM_PC_TO_PHONE:
                return new FileFromPcToPhoneStrategy(sync.asFile(), pcWorker, phoneWorker);
            case FOLDER_FROM_PHONE_TO_PC:
                return new FolderFromPhoneToPcStrategy(sync.asFolder(), pcWorker);
            case FILE_FROM_PHONE_TO_PC:
                return new FileFromPhoneToPcStrategy(sync.asFile(), pcWorker, phoneWorker);
            default:
                throw new RuntimeException();
        }
    }

    public void connectPhone() {
        phoneWorker.connect();
    }

    public void disconnectPhone() {
        phoneWorker.disconnect();
    }

    public String getMD5(Sync sync) {
        return pcWorker.getMD5(sync);
    }

    public long getSyncSize(Sync sync) {
        return pcWorker.getSyncSize(sync);
    }

    public byte[] getFileContent(Sync sync) {
        return pcWorker.getFileContent(sync);
    }

    public boolean isContentEquals(List<Sync> list) {
        return pcWorker.isContentEquals(list);
    }
}



