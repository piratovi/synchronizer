package com.kolosov.synchronizer.service.lowLevel.phone;

import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.enums.Location;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@Primary
public class EsPhoneWorker extends PhoneWorker {

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
                String rightEncodingCurrentFileName = convertEncodingForWeakFtp(currentFileName);
                String relativePathForSyncCreation = convertEncodingForWeakFtp(createRelativePath(fromRootDir, currentFileName));
                relativePathForSyncCreation = removeFirstSlash(relativePathForSyncCreation);
                if (aFile.isDirectory()) {
                    FolderSync currentFolderSync;
                    if (parentFolderSync == null) {
                        currentFolderSync = createRootFolderSync(result, rightEncodingCurrentFileName, relativePathForSyncCreation);
                    } else {
                        currentFolderSync = createFolderSync(parentFolderSync, rightEncodingCurrentFileName, relativePathForSyncCreation);
                    }
                    listDirectory(dirToList, currentFileName, result, createRelativePath(fromRootDir, currentFileName), currentFolderSync);
                } else {
                    createFileSync(parentFolderSync, rightEncodingCurrentFileName, relativePathForSyncCreation);
                }
            }
        }
    }

    private String removeFirstSlash(String relativePath) {
        return relativePath.substring(1);
    }

    private String convertEncodingForWeakFtp(String fileName) {
        return new String(fileName.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    private String removeProblemSymbols(String relativePath) {
        String[] blackChars = {"«", "»", "й", "[", "]", "."};
        String[] arrayWithEmptyChars = new String[blackChars.length];
        Arrays.fill(arrayWithEmptyChars, "");
        return StringUtils.replaceEach(relativePath, blackChars, arrayWithEmptyChars);
    }

    @Override
    protected String convertPathForFtp(String relativePath) {
        String replaced = relativePath.replaceAll("\\\\", "/");
        return new String(replaced.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    }

}
