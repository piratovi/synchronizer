package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.exceptions.FileNotFoundException;
import com.kolosov.synchronizer.service.lowLevel.PhoneWorker;
import com.kolosov.synchronizer.service.lowLevel.pc.PcWorker;
import com.kolosov.synchronizer.utils.MergeSyncsUtils;
import com.kolosov.synchronizer.utils.SyncUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectOperationsService {
    //TODO make strategy and validator pattern

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

    public void transferFromPhoneToPc(Sync sync) {
        if (sync.isFolder()) {
            pcWorker.createFolder(sync.asFolder());
        } else {
            transferFromPhoneToPc(sync.asFile());
        }
    }

    public void transferFromPhoneToPc(FileSync fileSync) {
        try (
                InputStream inputStream = phoneWorker.getInputStreamFrom(fileSync);
                OutputStream outputStream = pcWorker.getOutputStreamTo(fileSync)
        ) {
            inputStream.transferTo(outputStream);
        } catch (Exception e) {
            throw new RuntimeException(fileSync.toString(), e);
        }
        phoneWorker.closeStream();
    }

    public void transferFromPcToPhone(Sync sync) {
        if (sync.isFolder()) {
            phoneWorker.createFolder(sync.asFolder());
        } else {
            transferFromPcToPhone(sync.asFile());
        }
    }

    private void transferFromPcToPhone(FileSync fileSync) {
        try (
                InputStream inputStream = pcWorker.getInputStreamFrom(fileSync);
                OutputStream outputStream = phoneWorker.getOutputStreamTo(fileSync)
        ) {
            inputStream.transferTo(outputStream);
        } catch (Exception e) {
            throw new RuntimeException(fileSync.toString(), e);
        }
        phoneWorker.closeStream();
    }

    public List<RootFolderSync> getMergedList() {
        List<RootFolderSync> pcFiles = pcWorker.collectSyncs();
        List<RootFolderSync> ftpFiles = phoneWorker.collectSyncs();
        List<RootFolderSync> result = new ArrayList<>(pcFiles);
        MergeSyncsUtils.mergeSyncs(ftpFiles, result);
        SyncUtils.processExtensions(result);
        return result;
    }

    public void disconnect() {
        phoneWorker.disconnect();
    }
}



