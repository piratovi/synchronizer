package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.repository.FileEntityRepo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Data
@Slf4j
public class FileService {

    public static final Path PATH_TO_MUSIC = Path.of("D:", "Music");

    private List<FileEntity> fileEntities;
    private Map<String, List<FileEntity>> fileEntitiesByExt;
    private final FileEntityRepo fileEntityRepo;

    @PostConstruct
    public void postConstruct() throws IOException {
        List<FileEntity> fileEntitiesFromRepo = fileEntityRepo.findAll();
        if (!fileEntitiesFromRepo.isEmpty()) {
            this.fileEntities = fileEntitiesFromRepo;
        } else {
            this.fileEntities = getFileEntitiesFromDisk();
        }
        this.fileEntitiesByExt = separateExtensions();
        System.out.printf("");
        System.out.printf("");
        log.info("postConstruct");
    }

    private List<FileEntity> getFileEntitiesFromDisk() throws IOException {
        List<FileEntity> fileEntities;
        try (Stream<Path> stream = Files.walk(PATH_TO_MUSIC)) {
            fileEntities = stream
                    .map(Path::toString)
                    .map(FileEntity::new)
                    .map(fileEntityRepo::save)
                    .collect(Collectors.toList());
        }
        return fileEntities;
    }

    private Map<String, List<FileEntity>> separateExtensions() {
        return fileEntities.stream()
                    .filter(fileEntity -> (new File(fileEntity.getAbsolutePath())).isFile())
                    .collect(Collectors.groupingBy(
                            file -> FilenameUtils.getExtension(file.getAbsolutePath()), Collectors.toList())
                    );
    }

    public Set<String> getExt() {
        return fileEntitiesByExt.keySet();
    }

    public List<FileEntity> getFileEntitiesByExt(String ext) {
        return fileEntitiesByExt.get(ext);
    }

    public void deleteById(Long id) throws IOException {
        FileEntity deleteItem = fileEntityRepo.findById(id).orElseThrow();
        Files.deleteIfExists(Path.of(deleteItem.absolutePath));
        fileEntityRepo.deleteById(id);
        fileEntities.remove(deleteItem);
        String extension = FilenameUtils.getExtension(deleteItem.getAbsolutePath());
        fileEntitiesByExt.get(extension).removeIf(fileEntity -> fileEntity.getId() == id);
    }
}
