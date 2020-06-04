package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.FolderSync;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface LowLevelWorker {

    List<RootFolderSync> collectSyncs();

    void delete(Sync sync);

    InputStream getInputStreamFrom(FileSync sync);

    OutputStream getOutputStreamTo(FileSync sync);

    void createFolder(FolderSync folderSync);
}
