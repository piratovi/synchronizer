package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.service.LocationService;
import com.kolosov.synchronizer.service.lowLevel.pc.PcWorker;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@SpringBootTest
class PcWorkerTest {

    @Autowired
    PcWorker pcWorker;
    @Autowired
    LocationService locationService;

    @Test
    void renaming() {
        TreeSync newTreeSync = pcWorker.getNewTreeSync();
        newTreeSync.getNestedSyncs()
                .map(sync -> locationService.getAbsolutePathForPc(sync))
                .forEach(path -> {
                    File file = new File(path);
                    String absolutePath = file.getAbsolutePath();
                    byte[] winEncodingBytes = getBytes(absolutePath);
                    String newFileName = new String(winEncodingBytes, Charset.forName("WINDOWS-1251"));
                    File newFile = new File(newFileName);
                    file.renameTo(newFile);
                });
    }

    @SneakyThrows
    private byte[] getBytes(String absolutePath) {
        return absolutePath.getBytes(StandardCharsets.UTF_8);
    }

}