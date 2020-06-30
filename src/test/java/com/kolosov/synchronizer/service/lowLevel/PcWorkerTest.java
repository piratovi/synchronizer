package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.service.lowLevel.pc.PcWorker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class PcWorkerTest {

    @Autowired
    PcWorker pcWorker;

    @Test
    void getFileRelativePaths() {
//        List<AbstractSync> fileRelativePaths = pcWorker.getFileRelativePaths();
//        System.out.println(fileRelativePaths);
    }

    @Test
    void collectSyncs() {
        List<RootFolderSync> syncs = pcWorker.collectSyncs();
        System.out.println(syncs.size());
    }
}