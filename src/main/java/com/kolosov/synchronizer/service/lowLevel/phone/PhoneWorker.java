package com.kolosov.synchronizer.service.lowLevel.phone;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.service.LocationService;
import com.kolosov.synchronizer.service.lowLevel.LowLevelWorker;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public abstract class PhoneWorker implements LowLevelWorker {

    @Autowired
    @Setter
    protected LocationService locationService;

    public final FTPClient ftpClient = new FTPClient();

    public static final Pattern SPLIT = Pattern.compile("/");

    @Value("${com.kolosov.synchronizer.ftp.url}")
    protected String ftpServerUrl;

    @Value("${com.kolosov.synchronizer.ftp.port}")
    protected Integer ftpServerPort;

    @Value("${com.kolosov.synchronizer.ftp.username}")
    protected String username;

    @Value("${com.kolosov.synchronizer.ftp.password}")
    protected String password;

    @SneakyThrows
    public void connect() {
        if (!ftpClient.isConnected()) {
            ftpClient.connect(ftpServerUrl, ftpServerPort);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            String rootPath = locationService.getRootPhone();
            ftpClient.changeWorkingDirectory(rootPath);
            log.info("Connected to FTP");
        }
    }

    @SneakyThrows
    @PreDestroy
    public void disconnect() {
        if (ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
            log.info("Disconnected from FTP");
        }
    }

    @Override
    public List<RootFolderSync> collectSyncs() {
        connect();
        List<RootFolderSync> syncList = new ArrayList<>();
        List<String> foldersWithAbsolutePath = locationService.getAbsolutePathsForPhoneFolders();
        for (String folderWithAbsolutePath : foldersWithAbsolutePath) {
            listDirectory(folderWithAbsolutePath, "", syncList, folderWithAbsolutePath, null);
        }
//        disconnect();
        return syncList;
    }

    protected abstract void listDirectory(String parentDir, String currentDir, List<RootFolderSync> result, String fromRootDir, FolderSync parentFolderSync);

    protected String appendFileName(String fromRootDir, String currentFileName) {
        return fromRootDir + "\\" + currentFileName;
    }

    @SneakyThrows
    protected void prepareCatalogs(String relativePath) {
        List<String> dirs = new ArrayList<>(Arrays.asList(SPLIT.split(relativePath)));
        dirs.remove(dirs.size() - 1);
        String result = locationService.getRootPhone();
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

    @Override
    @SneakyThrows
    //TODO всетаки сделать
    public void createFolder(FolderSync folderSync) {
        connect();
        String result = appendFileName(locationService.getRootPhone(), convertPathForFtp(folderSync.getRelativePath()));
        ftpClient.makeDirectory(result);

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

    @SneakyThrows
    @Override
    public OutputStream getOutputStreamTo(FileSync sync) {
        connect();
        String relativePath = convertPathForFtp(sync.relativePath);
        prepareCatalogs(relativePath);
        return ftpClient.storeFileStream(relativePath);
    }

    @SneakyThrows
    @Override
    public InputStream getInputStreamFrom(FileSync sync) {
        connect();
        String relativePath = convertPathForFtp(sync.relativePath);
        return ftpClient.retrieveFileStream(relativePath);
    }

    @SneakyThrows
    @Override
    public void delete(Sync sync) {
        connect();
        String pathToDelete = locationService.getRootPhone() + "/" + convertPathForFtp(sync.relativePath);
        if (sync.isFolder()) {
            removeDirectory(pathToDelete, "");
        } else {
            ftpClient.deleteFile(pathToDelete);
        }
    }

    protected String removeFirstSlash(String relativePath) {
        return relativePath.substring(1);
    }

    protected abstract String convertPathForFtp(String relativePath);
}
