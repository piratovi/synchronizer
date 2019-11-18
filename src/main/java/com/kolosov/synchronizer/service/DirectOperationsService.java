package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.domain.Location;
import com.kolosov.synchronizer.service.lowLevel.FtpWorker;
import lombok.RequiredArgsConstructor;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DirectOperationsService {

    private final FtpWorker ftpWorker;


    public List<String> findFilesByLocation(Location location) {
        if (location.equals(Location.PHONE)) {
            return ftpWorker.getFilePaths();
        } else {
            return findFiles(location.path);
        }
    }

    public List<String> findFiles(Path pathToMusic) {
        List<String> fileEntities;
        try (Stream<Path> stream = Files.walk(pathToMusic)) {
            fileEntities = stream
                    .skip(1)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error while reading from " + pathToMusic);
        }
        return fileEntities;
    }

    public void deleteFile(FileEntity fileEntity) {


//        if (location.equals(Location.PC)) {
//            try {
//                Files.delete(Path.of(fileEntity.getAbsolutePath()));
//            } catch (IOException e) {
//                throw new RuntimeException("Error while deleting " + fileEntity.getAbsolutePath());
//            }
//        } else {
//            try {
//                //TODO Rewrite
//                SmbFile sFile = new SmbFile(fileEntity.absolutePath);
//                sFile.delete();
//            } catch (MalformedURLException | SmbException e) {
//                throw new RuntimeException("Котик не инициализирован");
//            }
//        }

    }


}
