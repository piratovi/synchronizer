package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileEntity;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileService {

    public static final Path PATH_TO_MUSIC = Path.of("D:", "Music");

    private List<FileEntity> fileEntities;
    private Map<String, List<File>> collect;

    public FileService() throws IOException {
        try (Stream<Path> stream = Files.walk(PATH_TO_MUSIC)) {
            this.fileEntities = stream
                    .map(FileEntity::new)
                    .collect(Collectors.toList());
        }
    }

    public Set<String> getExt() {
        collect = fileEntities.stream()
                .map(p -> p.getAbsolutePath().toFile())
                .filter(File::isFile)
                .collect(Collectors.groupingBy(f -> FilenameUtils.getExtension(f.getAbsolutePath()), Collectors.toList()));
    }
}
