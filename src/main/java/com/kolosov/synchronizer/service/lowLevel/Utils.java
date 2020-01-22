package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.utils.LocationUtils;

import java.io.File;

public class Utils {

    public static String convertPathForFTP(String relativePath) {
        return relativePath.replaceAll("\\\\", "/");
    }

    static String getAbsolutePath(FileEntity fileEntity) {
        return LocationUtils.getPcRootPath() + File.separator + fileEntity.relativePath;
    }
}
