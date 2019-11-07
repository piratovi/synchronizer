package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.domain.Location;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DirectFileOperationsService {


    public List<String> findFilesByLocation(Location location) {
        return findFiles(location.path);
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

    public void deleteFile(FileEntity fileEntity, Location location) {
        if (location.equals(Location.PC)) {
            try {
                Files.delete(Path.of(fileEntity.getAbsolutePath()));
            } catch (IOException e) {
                throw new RuntimeException("Error while deleting " + fileEntity.getAbsolutePath());
            }
        } else {
            try {
                //TODO Rewrite
                SmbFile sFile = new SmbFile(fileEntity.absolutePath);
                sFile.delete();
            } catch (MalformedURLException | SmbException e) {
                throw new RuntimeException("Котик не инициализирован");
            }
        }
    }

}
