package com.kolosov.synchronizer.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileService {
    Map<String, File> fileMap;

    public FileService() throws IOException {
        Path pathToMusic = Path.of("D:", "Music");
        try (Stream<Path> stream = Files.walk(pathToMusic)) {
            this.fileMap = stream
                    .map(Path::toFile)
                    .collect(Collectors.toMap(File::getAbsolutePath, file -> file));
        }
    }

    public Set<String> getExt() {
        return fileMap.entrySet().stream()
                .filter(e -> e.getValue().isFile())
                .map(e -> FilenameUtils.getExtension(e.getKey()))
                .collect(Collectors.toSet());
    }
}
