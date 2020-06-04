package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.exceptions.FileNotFoundException;
import com.kolosov.synchronizer.service.lowLevel.FtpWorker;
import com.kolosov.synchronizer.service.lowLevel.PcWorker;
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

    private final FtpWorker ftpWorker;
    private final PcWorker pcWorker;

    public void delete(Sync sync) {
        if (!sync.existOnPhone && !sync.existOnPC) {
            throw new FileNotFoundException("File already deleted " + sync.relativePath);
        }
        Location location = null;
        if (sync.existOnPhone) {
            ftpWorker.delete(sync);
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
                InputStream inputStream = ftpWorker.getInputStreamFrom(fileSync);
                OutputStream outputStream = pcWorker.getOutputStreamTo(fileSync)
        ) {
            inputStream.transferTo(outputStream);
        } catch (Exception e) {
            throw new RuntimeException(fileSync.toString(), e);
        }
        ftpWorker.closeStream();
    }

    public void transferFromPcToPhone(Sync sync) {
        if (sync.isFolder()) {
            ftpWorker.createFolder(sync.asFolder());
        } else {
            transferFromPcToPhone(sync.asFile());
        }
    }

    public void transferFromPcToPhone(FileSync fileSync) {
        try (
                InputStream inputStream = pcWorker.getInputStreamFrom(fileSync);
                OutputStream outputStream = ftpWorker.getOutputStreamTo(fileSync)
        ) {
            inputStream.transferTo(outputStream);
        } catch (Exception e) {
            throw new RuntimeException(fileSync.toString(), e);
        }
        ftpWorker.closeStream();
    }

    public List<RootFolderSync> getMergedList() {
        List<RootFolderSync> pcFiles = pcWorker.collectSyncs();
        List<RootFolderSync> ftpFiles = ftpWorker.collectSyncs();
        List<RootFolderSync> result = new ArrayList<>(pcFiles);
        MergeSyncsUtils.mergeSyncs(ftpFiles, result);
        SyncUtils.processExtensions(result);
        return result;
    }

    public void disconnect() {
        ftpWorker.disconnect();
    }
}



