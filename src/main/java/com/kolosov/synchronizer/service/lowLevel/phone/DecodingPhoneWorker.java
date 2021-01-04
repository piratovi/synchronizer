package com.kolosov.synchronizer.service.lowLevel.phone;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.kolosov.synchronizer.enums.Location.PHONE;

@Component
@Slf4j
@Primary
public class DecodingPhoneWorker extends PhoneWorker {

    @SneakyThrows
    @Override
    protected void listDirectory(String directory, String currentDir, String fromRootDir, FolderSync parentFolderSync) {
        if (!currentDir.equals("")) {
            directory += "/" + currentDir;
        }
        FTPFile[] subFiles = ftpClient.listFiles(directory + "/");
        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile file : subFiles) {
                String fileName = file.getName();
                if (fileName.equals(".") || fileName.equals("..")) {
                    continue;
                }
                String rightEncodingCurrentFileName = convertEncodingForWeakFtp(fileName);
                if (file.isDirectory()) {
                    FolderSync currentFolderSync = new FolderSync(rightEncodingCurrentFileName, PHONE, parentFolderSync);
                    listDirectory(directory, fileName, appendFileName(fromRootDir, fileName), currentFolderSync);
                } else {
                    new FileSync(rightEncodingCurrentFileName, PHONE, parentFolderSync);
                }
            }
        }
    }

    private String convertEncodingForWeakFtp(String fileName) {
        return new String(fileName.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    @Override
    protected String convertPathForFtp(String relativePath) {
        String replaced = relativePath.replaceAll("\\\\", "/");
        return new String(replaced.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    }

}
