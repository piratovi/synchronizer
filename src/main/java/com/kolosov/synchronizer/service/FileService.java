package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.repository.FileEntityRepo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Data
@Slf4j
public class FileService {

    @Autowired
    PcDirectFileOperationsService pcDirectOperationsService;

    private List<FileEntity> fileEntities;
    private Map<String, List<FileEntity>> fileEntitiesByExt;
    private final FileEntityRepo fileEntityRepo;

    @PostConstruct
    public void postConstruct() {
        List<FileEntity> fileEntitiesFromRepo = fileEntityRepo.findAll();
        if (!fileEntitiesFromRepo.isEmpty()) {
            this.fileEntities = fileEntitiesFromRepo;
        } else {
            this.fileEntities = getFileEntitiesFromDisk();
        }
        this.fileEntitiesByExt = separateFilesByExtensions();
        log.info("postConstruct is done");
    }

    private List<FileEntity> getFileEntitiesFromDisk() {
        return pcDirectOperationsService.getFiles().stream()
                .map(FileEntity::new)
                .map(fileEntityRepo::save)
                .collect(Collectors.toList());
    }

    private Map<String, List<FileEntity>> separateFilesByExtensions() {
        return fileEntities.stream()
                .filter(FileEntity::getIsFile)
                .collect(Collectors.groupingBy(
                        FileEntity::getExt, Collectors.toList())
                );
    }

    public Set<String> getExtensions() {
        return fileEntitiesByExt.keySet();
    }

    public List<FileEntity> getFileEntitiesByExt(String ext) {
        return fileEntitiesByExt.get(ext);
    }

    public void deleteById(Long id) {
        FileEntity deleteItem = fileEntityRepo.findById(id).orElseThrow(RuntimeException::new);
        deleteFileEntity(deleteItem);
        fileEntitiesByExt = separateFilesByExtensions();
    }

    public void deleteExtAll(String ext) {
        deleteFileEntities(getFileEntitiesByExt(ext));
        fileEntitiesByExt = separateFilesByExtensions();
    }

    private void deleteFileEntity(FileEntity fileEntity) {
        pcDirectOperationsService.deleteFile(fileEntity);
        fileEntities.remove(fileEntity);
    }

    public List<FileEntity> getEmptyFolders() {
        return fileEntities.stream()
                .filter(fileEntity -> !fileEntity.getIsFile())
                .filter(fileEntity -> {
                    File file = new File(fileEntity.getAbsolutePath());
                    return file.listFiles().length == 0;
                })
                .collect(Collectors.toList());
    }

    public void deleteEmptyFolders() {
        deleteFileEntities(getEmptyFolders());

    }

    private void deleteFileEntities(List<FileEntity> emptyFolders) {
        for (FileEntity emptyFolder : emptyFolders) {
            deleteFileEntity(emptyFolder);
        }
    }
}
