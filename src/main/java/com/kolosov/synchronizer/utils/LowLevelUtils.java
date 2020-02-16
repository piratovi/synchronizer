package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.utils.LocationUtils;

import java.io.File;

public class LowLevelUtils {

    public static String convertPathForFTP(String relativePath) {
        return relativePath.replaceAll("\\\\", "/");
    }

    public static String getAbsolutePath(AbstractSync abstractSync) {
        return LocationUtils.getPcRootPath() + File.separator + abstractSync.relativePath;
    }
}
