package com.kolosov.synchronizer.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalcUtilsTest {

    @Test
    void calculateTransferSpeed() {
        assertEquals(100, CalcUtils.calculateTransferSpeed(100 * 1024 * 1024, 1000));
    }

    @Test
    void calculateTreeScanSpeed() {
        assertEquals(20, CalcUtils.calculateTreeScanSpeed(20, 1000));
    }
}