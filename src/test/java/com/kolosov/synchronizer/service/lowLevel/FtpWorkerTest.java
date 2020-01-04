package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.FileEntity;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class FtpWorkerTest {

    @Autowired
    FtpWorker ftpWorker;

    @Test
    void getFileRelativePaths() {
        List<Pair<String, Boolean>> fileRelativePaths = ftpWorker.getFileRelativePaths();
        assertFalse(fileRelativePaths.isEmpty());
    }

    //предварительно поместить файл "test" в папку "test"
    @Test
    void delete() throws IOException {
        ftpWorker.ftpClient.changeWorkingDirectory("/test");
        List<FTPFile> ftpFiles = Arrays.asList(ftpWorker.ftpClient.listFiles());
        ftpWorker.ftpClient.mlistFile("test");
        FTPFile test = getTestFile(ftpFiles);
        assertNotNull(test);
        ftpWorker.deleteFile(new FileEntity("test", null, null, null));
        ftpFiles = Arrays.asList(ftpWorker.ftpClient.listFiles());
        test = getTestFile(ftpFiles);
        assertNull(test);

    }

    private FTPFile getTestFile(List<FTPFile> ftpFiles) {
        return ftpFiles.stream()
                .filter(ftpFile -> ftpFile.getName().equals("test"))
                .findFirst()
                .orElse(null);
    }

}