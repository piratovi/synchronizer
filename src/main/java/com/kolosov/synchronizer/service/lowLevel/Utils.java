package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.domain.Location;

import java.io.File;

public class Utils {

    public static String convertPathForFTP(String relativePath) {
        return relativePath.replaceAll("\\\\", "/");
    }

    static String getAbsolutePath(FileEntity fileEntity) {
        return Location.PC.rootPath.toString() + File.separator + fileEntity.relativePath;
    }
}
