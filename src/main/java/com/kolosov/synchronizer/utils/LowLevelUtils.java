package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.domain.Sync;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class LowLevelUtils {

    public static String convertPathForFTP(String relativePath) {
        String replaced = relativePath.replaceAll("\\\\", "/");
        return new String(replaced.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    }

    public static String getAbsolutePath(Sync sync) {
        return LocationUtils.getPcRootPath() + File.separator + sync.relativePath;
    }
}
