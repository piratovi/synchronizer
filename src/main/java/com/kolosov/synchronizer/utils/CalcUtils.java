package com.kolosov.synchronizer.utils;

public class CalcUtils {

    /**
     * @return MegaBytes/second
     */
    public static float calculateSpeed(float bytes, float milliseconds) {
        return (bytes / (1024 * 1024)) / (milliseconds / 1000);
    }

}