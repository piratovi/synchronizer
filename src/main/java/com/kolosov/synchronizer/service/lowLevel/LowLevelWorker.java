package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.domain.Sync;

import java.io.InputStream;
import java.io.OutputStream;

public interface LowLevelWorker {

    TreeSync getNewTreeSync();

    void delete(Sync sync);

    InputStream getInputStreamFrom(FileSync sync);

    OutputStream getOutputStreamTo(FileSync sync);

    void createFolder(FolderSync folderSync);
}
