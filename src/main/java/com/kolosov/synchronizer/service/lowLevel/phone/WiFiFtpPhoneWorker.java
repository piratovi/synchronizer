package com.kolosov.synchronizer.service.lowLevel.phone;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.RootFolderSync;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class WiFiFtpPhoneWorker extends PhoneWorker {

    @SneakyThrows
    @Override
    protected void listDirectory(String parentDir, String currentDir, List<RootFolderSync> result, String fromRootDir, FolderSync parentFolderSync) {
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
                String relativePathForSyncCreation = createRelativePath(fromRootDir, currentFileName);
                relativePathForSyncCreation = removeFirstSlash(relativePathForSyncCreation);
                if (aFile.isDirectory()) {
                    FolderSync currentFolderSync;
                    if (parentFolderSync == null) {
                        currentFolderSync = createRootFolderSync(result, currentFileName, relativePathForSyncCreation);
                    } else {
                        currentFolderSync = createFolderSync(parentFolderSync, currentFileName, relativePathForSyncCreation);
                    }
                    listDirectory(dirToList, currentFileName, result, createRelativePath(fromRootDir, currentFileName), currentFolderSync);
                } else {
                    createFileSync(parentFolderSync, currentFileName, relativePathForSyncCreation);
                }
            }
        }
    }

    private String removeFirstSlash(String relativePath) {
        return relativePath.substring(1);
    }

    @Override
    protected String convertPathForFtp(String relativePath) {
        return relativePath.replaceAll("\\\\", "/");
    }

}
