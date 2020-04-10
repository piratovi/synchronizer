package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.exceptions.FileNotFoundException;
import com.kolosov.synchronizer.service.lowLevel.FtpWorker;
import com.kolosov.synchronizer.service.lowLevel.PcWorker;
import com.kolosov.synchronizer.utils.MergeSyncsUtils;
import com.kolosov.synchronizer.utils.SyncUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectOperationsService {

    private final FtpWorker ftpWorker;
    private final PcWorker pcWorker;

    public void delete(Sync sync) {
        if (!sync.existOnPhone && !sync.existOnPC) {
            throw new FileNotFoundException("File already deleted " + sync.relativePath);
        }
        if (sync.existOnPhone) {
            ftpWorker.deleteFile(sync);
        }
        if (sync.existOnPC) {
            pcWorker.deleteFile(sync);
        }
    }

    public void copyFileFromPhoneToPc(Sync sync) {
        try (
                InputStream inputStream = ftpWorker.getInputStreamFromFile(sync);
                OutputStream outputStream = pcWorker.getOutputStreamToFile(sync)
        ) {
            inputStream.transferTo(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ftpWorker.closeStream();

    }

    public void copyFileFromPcToPhone(Sync sync) {
        try (
                InputStream inputStream = pcWorker.getInputStreamFromFile(sync);
                OutputStream outputStream = ftpWorker.getOutputStreamToFile(sync)
        ) {
            inputStream.transferTo(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ftpWorker.closeStream();
    }

    public List<FolderSync> getMergedList() {
        List<FolderSync> pcFiles = pcWorker.collectSyncs();
        List<FolderSync> ftpFiles = ftpWorker.collectSyncs();
        List<FolderSync> result = new ArrayList<>(pcFiles);
        MergeSyncsUtils.mergeSyncs(ftpFiles, result);
        SyncUtils.processExtensions(result);
        return result;
    }

}



