package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.domain.Location;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    public void deleteFile(FileEntity fileEntity) {
        try {
            Files.delete(Path.of(fileEntity.getAbsolutePath()));
        } catch (IOException e) {
            throw new RuntimeException("Error while deleting " + fileEntity.getAbsolutePath());
        }
    }

}
