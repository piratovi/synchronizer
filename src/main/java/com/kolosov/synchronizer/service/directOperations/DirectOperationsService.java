package com.kolosov.synchronizer.service.directOperations;

import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.enums.Location;
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
import com.kolosov.synchronizer.utils.SyncUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        Location location = null;
        if (sync.existOnPhone) {
            phoneWorker.delete(sync);
            location = Location.PHONE;
        }
        if (sync.existOnPC) {
            pcWorker.delete(sync);
            location = Location.PC;
        }
        log.info("{} deleted from {}", sync.relativePath, location);
    }

    public List<RootFolderSync> getMergedList() {
        List<RootFolderSync> pcFiles = pcWorker.collectSyncs();
        List<RootFolderSync> ftpFiles = phoneWorker.collectSyncs();
        List<RootFolderSync> result = new ArrayList<>(pcFiles);
        MergeSyncsUtils.mergeSyncs(ftpFiles, result);
        SyncUtils.processExtensions(result);
        return result;
    }

    public void transfer(Sync sync, TransferType transferType) {
        TransferStrategy transferStrategy = getTransferStrategy(sync, transferType);
        transferStrategy.transfer();
    }

    private TransferStrategy getTransferStrategy(Sync sync, TransferType transferType) {
        switch (transferType) {
            case FOLDER_FROM_PC_TO_PHONE:
                return new FolderFromPcToPhoneStrategy(sync, phoneWorker);
            case FILE_FROM_PC_TO_PHONE:
                return new FileFromPcToPhoneStrategy(sync, pcWorker, phoneWorker);
            case FOLDER_FROM_PHONE_TO_PC:
                return new FolderFromPhoneToPcStrategy(sync, pcWorker);
            case FILE_FROM_PHONE_TO_PC:
                return new FileFromPhoneToPcStrategy(sync, pcWorker, phoneWorker);
            default:
                throw new RuntimeException();
        }
    }

    public void connect() {
        phoneWorker.connect();
    }

    public void disconnect() {
        phoneWorker.disconnect();
    }
}



