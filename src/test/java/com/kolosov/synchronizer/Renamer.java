package com.kolosov.synchronizer;

import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.service.LocationService;
import com.kolosov.synchronizer.service.lowLevel.pc.PcWorker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class Renamer implements CommandLineRunner {

    private final PcWorker pcWorker;
    private final LocationService locationService;

    @Override
    public void run(String... args) {
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
