package com.kolosov.synchronizer.service.directOperations;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.exceptions.ExceptionSupplier;
import com.kolosov.synchronizer.service.directOperations.transferStrategy.FileFromPcToPhoneStrategy;
import com.kolosov.synchronizer.service.directOperations.transferStrategy.FileFromPhoneToPcStrategy;
import com.kolosov.synchronizer.service.directOperations.transferStrategy.FolderFromPcToPhoneStrategy;
import com.kolosov.synchronizer.service.directOperations.transferStrategy.FolderFromPhoneToPcStrategy;
import com.kolosov.synchronizer.service.directOperations.transferStrategy.TransferStrategy;
import com.kolosov.synchronizer.service.lowLevel.LowLevelWorker;
import com.kolosov.synchronizer.service.lowLevel.pc.PcWorker;
import com.kolosov.synchronizer.service.lowLevel.phone.PhoneWorker;
import com.kolosov.synchronizer.service.transporter.validator.TransferType;
import com.kolosov.synchronizer.utils.CalcUtils;
import com.kolosov.synchronizer.utils.MergeSyncUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

import static com.kolosov.synchronizer.enums.Location.PC;
import static com.kolosov.synchronizer.enums.Location.PHONE;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectOperationsService {

    private final PhoneWorker phoneWorker;
    private final PcWorker pcWorker;

    public void delete(Sync sync) {
        if (!sync.existOnPhone && !sync.existOnPc) {
            throw ExceptionSupplier.syncNotFound(sync).get();
        }
        if (sync.existOnPhone) {
            removeFromPhone(sync);
        }
        if (sync.existOnPc) {
            removeFromPc(sync);
        }
    }

    private void removeFromPhone(Sync sync) {
        try {
            phoneWorker.delete(sync);
        } catch (IOException exception) {
            String syncType = sync.isFile() ? "file" : "folder";
            throw new RuntimeException(String.format("Problem with removing %s %s from phone", syncType, sync.relativePath), exception);
        }
        log.info(String.format("Deleted from %7s : %s", PHONE, sync.relativePath));
    }

    private void removeFromPc(Sync sync) {
        try {
            pcWorker.delete(sync);
        } catch (IOException exception) {
            throw new RuntimeException(String.format("Problem with removing %s from pc", sync.relativePath), exception);
        }
        log.info(String.format("Deleted from %7s : %s", PC, sync.relativePath));
    }

    @SneakyThrows
    public TreeSync getNewTreeSync() {
        Callable<TreeSync> callablePc = () -> getTreeSync(pcWorker);
        Callable<TreeSync> callablePhone = () -> getTreeSync(phoneWorker);

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<TreeSync> treeSyncFuturePhone = executorService.submit(callablePhone);
        Future<TreeSync> treeSyncFuturePc = executorService.submit(callablePc);

        TreeSync pcTreeSync = treeSyncFuturePc.get();
        TreeSync ftpTreeSync = treeSyncFuturePhone.get();

        return MergeSyncUtils.mergeTrees(pcTreeSync, ftpTreeSync);
    }

    private TreeSync getTreeSync(LowLevelWorker lowLevelWorker) {
        StopWatch watch = new StopWatch();
        watch.start();
        TreeSync newTreeSync = lowLevelWorker.getNewTreeSync();
        watch.stop();
        float milliseconds = watch.getTime(TimeUnit.MILLISECONDS);
        long seconds = watch.getTime(TimeUnit.SECONDS);
        long quantity = newTreeSync.getNestedSyncs().count();
        float speed = CalcUtils.calculateTreeScanSpeed(quantity, milliseconds);
        log.info(String.format(
                "Tree from %5s scanned. Quantity = %d syncs. Time = %3d seconds. Speed = %8.1f syncs/second",
                lowLevelWorker.getLocation(),
                quantity,
                seconds,
                speed)
        );
        return newTreeSync;
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
                throw new RuntimeException("Ошибка в выборе стратегии копирования");
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

    public long getSyncSize(FileSync fileSync) {
        return pcWorker.getFileSyncSize(fileSync);
    }

    public byte[] getFileContent(Sync sync) {
        return pcWorker.getFileContent(sync);
    }

    public boolean isContentEquals(List<Sync> list) {
        return pcWorker.isContentEquals(list);
    }
}



