package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.FileEntity;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FtpWorker implements LowLevelWorker {

    private final FTPClient ftpClient = new FTPClient();
    @Value("${com.kolosov.synchronizer.ftpServerUrl}")
    private String ftpServerUrl;
    @Value("${com.kolosov.synchronizer.ftpServerPort}")
    private Integer ftpServerPort;
    @Value("${com.kolosov.synchronizer.username}")
    private String username;
    @Value("${com.kolosov.synchronizer.password}")
    private String password;

    @Override
    public List<String> getFilePaths() {
        try {
            ftpClient.connect(ftpServerUrl, ftpServerPort);
            ftpClient.login(username, password);
            ftpClient.changeWorkingDirectory("Music");
            List<String> fileList = new ArrayList<>();
//                ftpClient.deleteFile(new String("Общая/время.mp3".getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
            listDirectory(ftpClient, "/Music", "", fileList, "");
            List<String> collected = fileList.stream().map(s -> s.substring(1)).collect(Collectors.toList());
            ftpClient.logout();
            ftpClient.disconnect();
            return collected;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void listDirectory(FTPClient ftpClient, String parentDir, String currentDir, List<String> fileList, String fromRootDir) throws IOException {
        String dirToList = parentDir;
        if (!currentDir.equals("")) {
            dirToList += "/" + currentDir;
        }
        FTPFile[] subFiles = ftpClient.listFiles(dirToList);
        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();
                if (currentFileName.equals(".")|| currentFileName.equals("..")) {
                    // skip parent directory and directory itself
                    continue;
                }
                fileList.add(new String((fromRootDir + "\\" + currentFileName).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                if (aFile.isDirectory()) {
                    listDirectory(ftpClient, dirToList, currentFileName, fileList, fromRootDir + "\\" + currentFileName);
                }
            }
        }
    }

    @Override
    public void deleteFile(FileEntity fileEntity) {

    }
}
