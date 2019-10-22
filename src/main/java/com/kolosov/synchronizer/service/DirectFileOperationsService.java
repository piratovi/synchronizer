package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileEntity;

import java.util.List;

public interface DirectFileOperationsService {

    void deleteFile(FileEntity fileEntity);

    List<String> getFiles();



}
