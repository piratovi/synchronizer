package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.domain.Location;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FtpWorker implements LowLevelWorker {


    public static final Pattern SPLIT = Pattern.compile("/");

    @Value("${com.kolosov.synchronizer.ftpServerUrl}")
    private String ftpServerUrl;
    @Value("${com.kolosov.synchronizer.ftpServerPort}")
    private Integer ftpServerPort;
    @Value("${com.kolosov.synchronizer.username}")
    private String username;
    @Value("${com.kolosov.synchronizer.password}")
    private String password;

    public final FTPClient ftpClient = new FTPClient();
    private boolean connected = false;

    private void ftpConnect() throws IOException {
        if (!connected) {
            ftpClient.connect(ftpServerUrl, ftpServerPort);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.changeWorkingDirectory("Music");
            connected = true;
        }
    }

    @PostConstruct
    private void postConstruct() {
        try {
            ftpConnect();
        } catch (IOException e) {
            log.error("Can't connect to FTP" + e);
        }
    }

    @PreDestroy
    private void preDestroy() {
        try {
            ftpDisconnect();
        } catch (IOException e) {
            log.error("Can't disconnect from FTP" + e);
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
                if (currentFileName.equals(".") || currentFileName.equals("..")) {
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
            String pathToDelete = Utils.convertPathForFTP(fileEntity.relativePath);
            pathToDelete = new String(pathToDelete.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            ftpClient.deleteFile(pathToDelete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @Override
    public InputStream getInputStreamFromFile(FileEntity fileEntity) {
        String relativePath = fileEntity.relativePath;
        relativePath = Utils.convertPathForFTP(relativePath);
        return ftpClient.retrieveFileStream(relativePath);
    }

    @SneakyThrows
    @Override
    public OutputStream getOutputStreamToFile(FileEntity fileEntity) {
        String relativePath = fileEntity.relativePath;
        relativePath = Utils.convertPathForFTP(relativePath);
        prepareCatalogs(relativePath);
        return ftpClient.storeFileStream(relativePath);
    }

    @SneakyThrows
    private void prepareCatalogs(String relativePath) {
        List<String> dirs = new ArrayList<>(Arrays.asList(SPLIT.split(relativePath)));
        dirs.remove(dirs.size() - 1);
        String result = Location.PHONE.rootPath;
        for (int i = 0; i < dirs.size(); i++) {
            String current = dirs.get(i);
            List<String> names = Arrays.asList(ftpClient.listNames(result));
            result += "/" + current;
            if (!names.contains(current)) {
                ftpClient.makeDirectory(result);
            }
        }
    }

    @SneakyThrows
    public void closeStream() {
        ftpClient.completePendingCommand();
    }
}
