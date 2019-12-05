package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.FileEntity;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FtpWorker implements LowLevelWorker {


    @Value("${com.kolosov.synchronizer.ftpServerUrl}")
    private String ftpServerUrl;
    @Value("${com.kolosov.synchronizer.ftpServerPort}")
    private Integer ftpServerPort;
    @Value("${com.kolosov.synchronizer.username}")
    private String username;
    @Value("${com.kolosov.synchronizer.password}")
    private String password;

    private final FTPClient ftpClient = new FTPClient();
    private boolean connected = false;

    private void ftpConnect() throws IOException {
        if (!connected) {
            ftpClient.connect(ftpServerUrl, ftpServerPort);
            ftpClient.login(username, password);
            ftpClient.changeWorkingDirectory("Music");
            connected = true;
        }
    }

    private void ftpDisconnect() throws IOException {
        ftpClient.logout();
        ftpClient.disconnect();
    }

    @Override
    public List<Pair<String, Boolean>> getFileRelativePaths() {
        try {
            ftpConnect();
            List<Pair<String, Boolean>> fileList = new ArrayList<>();
            listDirectory(ftpClient, "/Music", "", fileList, "");
            List<Pair<String, Boolean>> collected = fileList.stream().map(s -> {
                String relativePath = s.getFirst().substring(1);
                return Pair.of(relativePath, s.getSecond());
            }).collect(Collectors.toList());
            return collected;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void listDirectory(FTPClient ftpClient, String parentDir, String currentDir, List<Pair<String, Boolean>> fileList, String fromRootDir) throws IOException {
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
                String relativePath = new String((fromRootDir + "\\" + currentFileName).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                if (aFile.isDirectory()) {
                    fileList.add(Pair.of(relativePath, false));
                    listDirectory(ftpClient, dirToList, currentFileName, fileList, fromRootDir + "\\" + currentFileName);
                } else {
                    fileList.add(Pair.of(relativePath, true));
                }
            }
        }
    }

    @Override
    public void deleteFile(FileEntity fileEntity) {
        try {
            ftpConnect();
            String pathToDelete = fileEntity.relativePath.replaceAll("\\\\", "/");
            pathToDelete = new String(pathToDelete.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            ftpClient.deleteFile(pathToDelete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream getInputStreamFromFile(FileEntity fileEntity) {
        return null;
    }

    @Override
    public void copyFile(InputStream inputStream, FileEntity fileEntity) {

    }
}
