package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.FolderSync;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface LowLevelWorker {

    List<FolderSync> collectSyncs();

    void deleteFile(AbstractSync abstractSync);

    InputStream getInputStreamFromFile(AbstractSync abstractSync);

    OutputStream getOutputStreamToFile(AbstractSync abstractSync);
}
