package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.domain.Sync;

import java.io.File;

public class LowLevelUtils {

    public static String convertPathForFTP(String relativePath) {
        return relativePath.replaceAll("\\\\", "/");
    }

    public static String getAbsolutePath(Sync sync) {
        return LocationUtils.getPcRootPath() + File.separator + sync.relativePath;
    }
}
