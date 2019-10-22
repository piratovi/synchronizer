package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PcDirectFileOperationsService implements DirectFileOperationsService {

    public static final Path PATH_TO_MUSIC = Path.of("C:", "Users", "pirat", "Music");

    @Override
    public void deleteFile(FileEntity fileEntity) {
        try {
            Files.delete(Path.of(fileEntity.getAbsolutePath()));
        } catch (IOException e) {
            throw new RuntimeException("Error while deleting " + fileEntity.getAbsolutePath());
        }
    }

    @Override
    public List<String> getFiles() {
        List<String> fileEntities;
        try (Stream<Path> stream = Files.walk(PcDirectFileOperationsService.PATH_TO_MUSIC)) {
            fileEntities = stream
                    .skip(1)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error while reading from disk");
        }
        return fileEntities;
    }
}
