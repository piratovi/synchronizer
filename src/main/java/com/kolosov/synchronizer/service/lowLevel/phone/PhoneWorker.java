package com.kolosov.synchronizer.service.lowLevel.phone;

import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.service.lowLevel.LowLevelWorker;
import com.kolosov.synchronizer.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@RequiredArgsConstructor
public class PhoneWorker implements LowLevelWorker {

    private final LocationService locationService;
    public static final Pattern SPLIT = Pattern.compile("/");

    @Value("${com.kolosov.synchronizer.ftp.url}")
    private String ftpServerUrl;
    @Value("${com.kolosov.synchronizer.ftp.port}")
    private Integer ftpServerPort;
    @Value("${com.kolosov.synchronizer.ftp.username}")
    private String username;
    @Value("${com.kolosov.synchronizer.ftp.password}")
    private String password;

    public final FTPClient ftpClient = new FTPClient();

    @SneakyThrows
    public void connect() {
        if (!(ftpClient.isConnected() && ftpClient.isAvailable())) {
            ftpClient.connect(ftpServerUrl, ftpServerPort);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            String rootPath = locationService.getRootPhone();
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
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
                log.info("Disconnected from FTP");
            }
        } catch (IOException e) {
            ftpClient.disconnect();
        }
    }

    @Override
    public List<RootFolderSync> collectSyncs() {
        List<RootFolderSync> syncList = new ArrayList<>();
        List<String> folders = locationService.getAbsolutePathsForPhoneFolders();
        for (String folder : folders) {
            listDirectory(folder, "", syncList, folder, null);
        }
        return syncList;
    }

    @SneakyThrows
    private void listDirectory(String parentDir, String currentDir, List<RootFolderSync> result, String fromRootDir, FolderSync parentFolderSync) {
        String dirToList = parentDir;
        if (!currentDir.equals("")) {
            dirToList += "/" + currentDir;
        }
        FTPFile[] subFiles = ftpClient.listFiles(dirToList);
        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = convertEncodingForWeakFtp(aFile.getName());
                if (currentFileName.equals(".") || currentFileName.equals("..")) {
                    continue;
                }
                String relativePath = createRelativePath(fromRootDir, currentFileName);
                relativePath = removeFirstSlash(relativePath);
                if (aFile.isDirectory()) {
                    FolderSync currentFolderSync;
                    if (parentFolderSync == null) {
                        currentFolderSync = createRootFolderSync(result, currentFileName, relativePath);
                    } else {
                        currentFolderSync = createFolderSync(parentFolderSync, currentFileName, relativePath);
                    }
                    listDirectory(dirToList, currentFileName, result, createRelativePath(fromRootDir, currentFileName), currentFolderSync);
                } else {
                    createFileSync(parentFolderSync, currentFileName, relativePath);
                }
            }
        }
    }

    private void createFileSync(FolderSync parentFolderSync, String currentFileName, String relativePath) {
        if (parentFolderSync == null) {
            throw new RuntimeException("fileSync without parent");
        } else {
            FileSync fileSync = new FileSync(relativePath, currentFileName, Location.PHONE, parentFolderSync);
            parentFolderSync.list.add(fileSync);
        }
    }

    private FolderSync createFolderSync(FolderSync parentFolderSync, String currentFileName, String relativePath) {
        FolderSync folderSync = new FolderSync(relativePath, currentFileName, Location.PHONE, parentFolderSync);
        parentFolderSync.list.add(folderSync);
        return folderSync;
    }

    private FolderSync createRootFolderSync(List<RootFolderSync> result, String currentFileName, String relativePath) {
        FolderSync folderSync = new RootFolderSync(relativePath, currentFileName, Location.PHONE);
        result.add(folderSync.asRootFolder());
        return folderSync;
    }

    private String createRelativePath(String fromRootDir, String currentFileName) {
        return fromRootDir + "\\" + currentFileName;
    }

    private String removeFirstSlash(String relativePath) {
        return relativePath.substring(1);
    }

    private String convertEncodingForWeakFtp(String fileName) {
        return new String(fileName.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    @Override
    @SneakyThrows
    public void delete(Sync sync) {
        String pathToDelete = locationService.getRootPhone() + "/" + convertPathForFtp(sync.relativePath);
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
        relativePath = convertPathForFtp(relativePath);
        return ftpClient.retrieveFileStream(relativePath);
    }

    @SneakyThrows
    @Override
    public OutputStream getOutputStreamTo(FileSync sync) {
        String relativePath = sync.relativePath;
//        relativePath = removeProblemSymbols(relativePath);
        relativePath = convertPathForFtp(relativePath);
        prepareCatalogs(relativePath);
        return ftpClient.storeFileStream(relativePath);
    }

    private String removeProblemSymbols(String relativePath) {
        String[] blackChars = {"«", "»", "й", "[", "]", "."};
        String[] arrayWithEmptyChars = new String[blackChars.length];
        Arrays.fill(arrayWithEmptyChars, "");
        return StringUtils.replaceEach(relativePath, blackChars, arrayWithEmptyChars);
    }

    @SneakyThrows
    private void prepareCatalogs(String relativePath) {
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

    private String convertPathForFtp(String relativePath) {
        String replaced = relativePath.replaceAll("\\\\", "/");
        return new String(replaced.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    }

}
