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
        log.info("postConstruct is done");
    }

    private List<FileEntity> getFileEntitiesFromDisk() {
        List<FileEntity> fileEntities;
        try (Stream<Path> stream = Files.walk(PATH_TO_MUSIC)) {
            fileEntities = stream
                    .skip(1)
                    .map(Path::toString)
                    .map(FileEntity::new)
                    .map(fileEntityRepo::save)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error while reading from disk");
        }
        return fileEntities;
    }

    private Map<String, List<FileEntity>> separateExtensions() {
        return fileEntities.stream()
                .filter(fileEntity -> (new File(fileEntity.getAbsolutePath())).isFile())
                .collect(Collectors.groupingBy(
                        file -> FilenameUtils.getExtension(file.getAbsolutePath().toLowerCase()), Collectors.toList())
                );
    }

    public Set<String> getExt() {
        return fileEntitiesByExt.keySet();
    }

    public List<FileEntity> getFileEntitiesByExt(String ext) {
        return fileEntitiesByExt.get(ext);
    }

    public void deleteById(Long id) {
        FileEntity deleteItem = fileEntityRepo.findById(id).orElseThrow();
        deleteFileFromDisk(deleteItem);
        refresh();
    }

    private void deleteFileFromDisk(FileEntity deleteItem) {
        try {
            Files.delete(Path.of(deleteItem.absolutePath));
        } catch (IOException e) {
            throw new RuntimeException("Error while deleting " + deleteItem.getAbsolutePath());
        }
    }

    public void deleteExtAll(String ext) {
        for (FileEntity fileEntity : getFileEntitiesByExt(ext)) {
            deleteFileFromDisk(fileEntity);
        }
        refresh();
    }

    public void refresh() {
        log.info("refresh start");
        fileEntityRepo.deleteAll();
        this.fileEntities = getFileEntitiesFromDisk();
        this.fileEntitiesByExt = separateExtensions();
        log.info("refresh done");
    }

    public List<FileEntity> getEmptyFolders() {
        return fileEntities.stream()
                .filter(fileEntity -> {
                    final File file = new File(fileEntity.absolutePath);
                    return file.isDirectory() && file.listFiles().length == 0;
                })
                .collect(Collectors.toList());
    }

    public void deleteEmptyFolders() {
        for (FileEntity emptyFolder : getEmptyFolders()) {
            deleteFileFromDisk(emptyFolder);
        }
        refresh();
    }
}
