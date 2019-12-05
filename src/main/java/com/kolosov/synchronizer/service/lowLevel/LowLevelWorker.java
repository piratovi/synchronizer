package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.FileEntity;
import org.springframework.data.util.Pair;

import java.io.InputStream;
import java.util.List;

public interface LowLevelWorker {

    List<Pair<String, Boolean>> getFileRelativePaths();

    void deleteFile(FileEntity fileEntity);

    InputStream getInputStreamFromFile(FileEntity fileEntity);

    void copyFile(InputStream inputStream, FileEntity fileEntity);
}
