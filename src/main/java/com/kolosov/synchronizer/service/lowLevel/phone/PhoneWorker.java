package com.kolosov.synchronizer.service.lowLevel.phone;

import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.service.lowLevel.LowLevelWorker;
import com.kolosov.synchronizer.utils.LocationUtils;
import com.kolosov.synchronizer.utils.LowLevelUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
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
public class PhoneWorker implements LowLevelWorker {

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

    @SneakyThrows
    private void ftpConnect() {
        if (!(ftpClient.isConnected() && ftpClient.isAvailable())) {
            ftpClient.connect(ftpServerUrl, ftpServerPort);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            String rootPath = LocationUtils.getPhoneRootPath();
            ftpClient.changeWorkingDirectory(rootPath);
            log.info("Connected to FTP");
        }
    }

    @PreDestroy
    private void tearDown() {
        disconnect();
    }

    @SneakyThrows
    public void disconnect() {
        ftpClient.logout();
        ftpClient.disconnect();
    }

    @Override
    @SneakyThrows
    public List<RootFolderSync> collectSyncs() {
        ftpConnect();
        List<RootFolderSync> syncList = new ArrayList<>();
        listDirectory(ftpClient, LocationUtils.getPhoneRootPath(), "", syncList, "", null);
        return syncList;
    }

    private void listDirectory(FTPClient ftpClient, String parentDir, String currentDir, List<RootFolderSync> result, String fromRootDir, FolderSync parentFolderSync) throws IOException {
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
                        nextFolderSync = new RootFolderSync(relativePath, currentFileName, Location.PHONE);
                        result.add(nextFolderSync.asRootFolder());
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
    public void delete(Sync sync) {
        ftpConnect();
        String pathToDelete = LocationUtils.getPhoneRootPath() + "/" + LowLevelUtils.convertPathForFTP(sync.relativePath);
        if (sync instanceof FolderSync) {
            removeDirectory(pathToDelete, "");
        } else {
            ftpClient.deleteFile(pathToDelete);
        }

    }

    @SneakyThrows
    @Override
    public InputStream getInputStreamFrom(FileSync sync) {
        String relativePath = sync.relativePath;
        relativePath = LowLevelUtils.convertPathForFTP(relativePath);
        return ftpClient.retrieveFileStream(relativePath);
    }

    @SneakyThrows
    @Override
    public OutputStream getOutputStreamTo(FileSync sync) {
        String relativePath = sync.relativePath;
        relativePath = LowLevelUtils.convertPathForFTP(relativePath);
        prepareCatalogs(relativePath);
        return ftpClient.storeFileStream(relativePath);
    }

    @SneakyThrows
    private void prepareCatalogs(String relativePath) {
        ftpConnect();
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
        }
        // finally, remove the directory itself
        boolean removed = ftpClient.removeDirectory(dirToList);
        if (removed) {
            System.out.println("REMOVED the directory: " + dirToList);
        } else {
            System.out.println("CANNOT remove the directory: " + dirToList);
        }

    }

    @Override
    public void createFolder(FolderSync folderSync) {
        //empty
    }
}
