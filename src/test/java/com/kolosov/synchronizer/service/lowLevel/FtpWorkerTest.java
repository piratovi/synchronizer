package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.FileEntity;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        FTPFile test = ftpWorker.ftpClient.listFiles("", file -> file.getName().equals("test"))[0];
        assertNotNull(test);
        ftpWorker.deleteFile(new FileEntity("test", null, null, null));
        FTPFile[] testFiles = ftpWorker.ftpClient.listFiles("", file -> file.getName().equals("test"));
        assertEquals(0, testFiles.length);
    }

    @Test
    void simple() {
        System.out.println();
    }

}