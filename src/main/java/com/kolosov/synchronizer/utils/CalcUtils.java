package com.kolosov.synchronizer.utils;

public class CalcUtils {

    /**
     * @return MegaBytes/second
     */
    public static float calculateTransferSpeed(float bytes, float milliseconds) {
        return (bytes / (1024 * 1024)) / (milliseconds / 1000);
    }

    public static float calculateTreeScanSpeed(long quantity, float milliseconds) {
        return quantity / (milliseconds / 1000);
    }
}