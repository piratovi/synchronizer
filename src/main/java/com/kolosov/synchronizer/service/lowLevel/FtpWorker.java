package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.utils.LocationUtils;
import com.kolosov.synchronizer.utils.LowLevelUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
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

//    @EventListener(ApplicationReadyEvent.class)
//    public void doSomethingAfterStartup() {
//        try {
//            ftpConnect();
//        } catch (Exception e) {
//            log.error("Can't connect to FTP " + e.getMessage());
//        }
//    }

    //TODO обработка не подключенного фтп
    @SneakyThrows
    private void ftpConnect() {
        connected = ftpClient.isConnected() && ftpClient.isAvailable();
        if (!connected) {
            ftpClient.connect(ftpServerUrl, ftpServerPort);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            String rootPath = LocationUtils.getPhoneRootPath();
            ftpClient.changeWorkingDirectory(rootPath);
            connected = true;
            log.info("Connect to FTP");
        }
    }

    @PreDestroy
    private void tearDown() {
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
    @SneakyThrows
    public List<FolderSync> collectSyncs() {
        ftpConnect();
        List<FolderSync> syncList = new ArrayList<>();
        listDirectory(ftpClient, LocationUtils.getPhoneRootPath(), "", syncList, "", null);
        return syncList;
    }

    private void listDirectory(FTPClient ftpClient, String parentDir, String currentDir, List<FolderSync> result, String fromRootDir, FolderSync parentFolderSync) throws IOException {
        String dirToList = parentDir;
        if (!currentDir.equals("")) {
            dirToList += "/" + currentDir;
        }
        FTPFile[] subFiles = ftpClient.listFiles(dirToList);
        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();
                if (currentFileName.equals(".") || currentFileName.equals("..")) {
                    continue;
                }
                String relativePath = new String((fromRootDir + "\\" + currentFileName).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                relativePath = relativePath.substring(1);
                if (aFile.isDirectory()) {
                    FolderSync nextFolderSync;
                    if (parentFolderSync == null) {
                        //Root Folder
                        nextFolderSync = new FolderSync(relativePath, currentFileName, Location.PHONE, null);
                        result.add(nextFolderSync);
                    } else {
                        nextFolderSync = new FolderSync(relativePath, currentFileName, Location.PHONE, parentFolderSync);
                        parentFolderSync.list.add(nextFolderSync);
                    }
                    listDirectory(ftpClient, dirToList, currentFileName, result, fromRootDir + "\\" + currentFileName, nextFolderSync);
                } else {
                    if (parentFolderSync != null) {
                        parentFolderSync.list.add(new FileSync(relativePath, currentFileName, Location.PHONE, parentFolderSync));
                    }
                }
            }
        }
    }

    @Override
    @SneakyThrows
    public void deleteFile(AbstractSync sync) {
        ftpConnect();
        String pathToDelete = LocationUtils.getPhoneRootPath() + "/" + LowLevelUtils.convertPathForFTP(sync.relativePath);
        pathToDelete = new String(pathToDelete.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        if (sync instanceof FolderSync) {
            removeDirectory(pathToDelete, "");
        } else {
            ftpClient.deleteFile(pathToDelete);
        }

    }

    @SneakyThrows
    @Override
    public InputStream getInputStreamFromFile(AbstractSync abstractSync) {
        String relativePath = abstractSync.relativePath;
        relativePath = LowLevelUtils.convertPathForFTP(relativePath);
        return ftpClient.retrieveFileStream(relativePath);
    }

    @SneakyThrows
    @Override
    public OutputStream getOutputStreamToFile(AbstractSync abstractSync) {
        String relativePath = abstractSync.relativePath;
        relativePath = LowLevelUtils.convertPathForFTP(relativePath);
        prepareCatalogs(relativePath);
        return ftpClient.storeFileStream(relativePath);
    }

    @SneakyThrows
    private void prepareCatalogs(String relativePath) {
        List<String> dirs = new ArrayList<>(Arrays.asList(SPLIT.split(relativePath)));
        dirs.remove(dirs.size() - 1);
        String result = LocationUtils.getPhoneRootPath();
        for (String current : dirs) {
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


    public void removeDirectory(String parentDir, String currentDir) throws IOException {
        String dirToList = parentDir;
        if (!currentDir.equals("")) {
            dirToList += "/" + currentDir;
        }

        FTPFile[] subFiles = ftpClient.listFiles(dirToList);

        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();
                if (currentFileName.equals(".") || currentFileName.equals("..")) {
                    // skip parent directory and the directory itself
                    continue;
                }
                String filePath = parentDir + "/" + currentDir + "/"
                        + currentFileName;
                if (currentDir.equals("")) {
                    filePath = parentDir + "/" + currentFileName;
                }

                if (aFile.isDirectory()) {
                    // remove the sub directory
                    removeDirectory(dirToList, currentFileName);
                } else {
                    // delete the file
                    boolean deleted = ftpClient.deleteFile(filePath);
                    if (deleted) {
                        System.out.println("DELETED the file: " + filePath);
                    } else {
                        System.out.println("CANNOT delete the file: "
                                + filePath);
                    }
                }
            }

            // finally, remove the directory itself
            boolean removed = ftpClient.removeDirectory(dirToList);
            if (removed) {
                System.out.println("REMOVED the directory: " + dirToList);
            } else {
                System.out.println("CANNOT remove the directory: " + dirToList);
            }
        }
    }
}
