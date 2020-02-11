package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.AbstractSync;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PcWorkerTest {

    @Autowired
    PcWorker pcWorker;

    @Test
    void getFileRelativePaths() {
        List<AbstractSync> fileRelativePaths = pcWorker.getFileRelativePaths();
        System.out.println(fileRelativePaths);
    }
}