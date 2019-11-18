package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.FileEntity;

import java.util.List;

public interface LowLevelWorker {

    List<String> getFilePaths();

    void deleteFile(FileEntity fileEntity);
}
