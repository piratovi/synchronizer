package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.enums.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LocationUtilsTest {

    @Autowired
    LocationUtils locationUtils;

    @Test
    void test() {
        System.out.println(LocationUtils.getRootPath(Location.PC));
        System.out.println(LocationUtils.getRootPath(Location.PHONE));
    }

}