package com.kolosov.synchronizer.utils;

public class CalcUtils {

    /**
     * @return Megabytes/second
     */
    public static float calculateTransferSpeed(float bytes, float milliseconds) {
        float megabytes = bytes / (1024 * 1024);
        float seconds = milliseconds / 1000;
        return megabytes / seconds;
    }

    /**
     * @return Syncs/second
     */
    public static float calculateTreeScanSpeed(float quantity, float milliseconds) {
        float seconds = milliseconds / 1000;
        return quantity / seconds;
    }
}