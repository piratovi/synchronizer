package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.AbstractSync;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface LowLevelWorker {

    List<AbstractSync> getFileRelativePaths();

    void deleteFile(AbstractSync abstractSync);

    InputStream getInputStreamFromFile(AbstractSync abstractSync);

    OutputStream getOutputStreamToFile(AbstractSync abstractSync);
}
