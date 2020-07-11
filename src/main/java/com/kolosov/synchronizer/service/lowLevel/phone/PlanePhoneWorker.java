package com.kolosov.synchronizer.service.lowLevel.phone;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.RootFolderSync;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.kolosov.synchronizer.enums.Location.PHONE;

@Component
@Slf4j
public class PlanePhoneWorker extends PhoneWorker {

    @SneakyThrows
    @Override
    protected void listDirectory(String directory, String fileName, List<RootFolderSync> rootFolderSyncs, String absolutePath, FolderSync parentFolderSync) {
        if (!fileName.equals("")) {
            directory += "/" + fileName;
        }
        FTPFile[] subFiles = ftpClient.listFiles(directory + "/");
        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile file : subFiles) {
                fileName = file.getName();
                if (fileName.equals(".") || fileName.equals("..")) {
                    continue;
                }
                absolutePath = appendFileName(absolutePath, fileName);
                String relativePathForSyncCreation = removeFirstSlash(absolutePath);
                if (file.isDirectory()) {
                    FolderSync currentFolderSync;
                    if (parentFolderSync == null) {
                        currentFolderSync = new RootFolderSync(relativePathForSyncCreation, fileName, PHONE);
                        rootFolderSyncs.add(currentFolderSync.asRootFolder());
                    } else {
                        currentFolderSync = new FolderSync(relativePathForSyncCreation, fileName, PHONE, parentFolderSync);
                    }
                    listDirectory(directory, fileName, rootFolderSyncs, absolutePath, currentFolderSync);
                } else {
                    new FileSync(relativePathForSyncCreation, fileName, PHONE, parentFolderSync);
                }
            }
        }
    }

    @Override
    protected String convertPathForFtp(String relativePath) {
        return relativePath.replaceAll("\\\\", "/");
    }

}
