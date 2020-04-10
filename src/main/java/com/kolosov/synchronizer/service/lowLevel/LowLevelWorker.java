package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.FolderSync;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface LowLevelWorker {

    List<FolderSync> collectSyncs();

    void deleteFile(Sync sync);

    InputStream getInputStreamFromFile(Sync sync);

    OutputStream getOutputStreamToFile(Sync sync);
}
