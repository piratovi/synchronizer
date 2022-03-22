package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.enums.Location;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface LowLevelWorker {

    TreeSync getNewTreeSync();

    void delete(Sync sync) throws IOException;

    InputStream getInputStreamFrom(FileSync sync);

    OutputStream getOutputStreamTo(FileSync sync);

    void createFolder(FolderSync folderSync) throws IOException;

    Location getLocation();

}
