package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.domain.Location;
import com.kolosov.synchronizer.repository.FileEntityRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Data
@Slf4j
public class FileService {

    private final DirectFileOperationsService directOperationsService;
    private final FileEntityRepository fileEntityRepository;

//    @PostConstruct
//    public void postConstruct() {
//        log.info("PostConstruct start");
//        init(Location.PC);
//        init(Location.PHONE);
//        log.info("PostConstruct end");
//    }

    private void init(Location location) {
        log.info("Init " + location.name() + " start");
        List<FileEntity> fileEntitiesFromDB = fileEntityRepository.findAllByLocation(location);
        if (fileEntitiesFromDB.isEmpty()) {
            if (!location.path.toFile().exists()) {
                throw new RuntimeException(location.path + " directory doesn't exist");
            } else {
                readFilesAndCreateFileEntities(location);
            }
        }
        log.info("Init " + location.name() + " end");
    }

    private List<FileEntity> readFilesAndCreateFileEntities(Location location) {
        return directOperationsService.findFilesByLocation(location).stream()
                .map(s -> new FileEntity(s, location))
                .map(fileEntityRepository::save)
                .collect(Collectors.toList());
    }

    private Map<String, List<FileEntity>> separateFileEntitiesByExtensions(List<FileEntity> fileEntities) {
        return fileEntities.stream()
                .filter(FileEntity::getIsFile)
                .collect(Collectors.groupingBy(
                        FileEntity::getExt, Collectors.toList()));
    }

    public Set<String> getExtensions(Location location) {
        List<FileEntity> fileEntities = getFileEntitiesByLocation(location);
        Map<String, List<FileEntity>> fileEntitiesSeparatedByExtensions = separateFileEntitiesByExtensions(fileEntities);
        return fileEntitiesSeparatedByExtensions.keySet();
    }

    public List<FileEntity> getFileEntitiesWithExt(Location location, String ext) {
        List<FileEntity> fileEntities = getFileEntitiesByLocation(location);
        Map<String, List<FileEntity>> fileEntitiesSeparatedByExtensions = separateFileEntitiesByExtensions(fileEntities);
        return fileEntitiesSeparatedByExtensions.get(ext);

    }

    public void deleteById(Long id) {
        //TODO Создать свой эксепшен?
        FileEntity fileEntityToDelete = fileEntityRepository.findById(id).orElseThrow(RuntimeException::new);
        deleteFileEntity(fileEntityToDelete);
    }

    public void deleteExtAll(Location location, String ext) {
        List<FileEntity> fileEntitiesWithExt = getFileEntitiesWithExt(location, ext);
        deleteFileEntities(fileEntitiesWithExt);
    }

    private void deleteFileEntity(FileEntity fileEntity) {
        directOperationsService.deleteFile(fileEntity);
        fileEntityRepository.delete(fileEntity);
    }

    public List<FileEntity> getEmptyFolders(Location location) {
        return fileEntityRepository.findAllByLocation(location).stream()
                .filter(fileEntity -> !fileEntity.isFile)
                .filter(fileEntity -> {
                    File file = new File(fileEntity.getAbsolutePath());
                    return Objects.requireNonNull(file.listFiles()).length == 0;
                })
                .collect(Collectors.toList());
    }

    public void deleteEmptyFolders(Location location) {
        deleteFileEntities(getEmptyFolders(location));
    }

    private void deleteFileEntities(List<FileEntity> fileEntities) {
        for (FileEntity fileEntity : fileEntities) {
            deleteFileEntity(fileEntity);
        }
    }

    public void refresh(Location location) {
        log.info("refresh" + location + " start");
        fileEntityRepository.deleteAllByLocation(location);
        readFilesAndCreateFileEntities(location);
        log.info("refresh" + location + " done");
    }

    public List<FileEntity> onlyOnPC() {
        return subtract(getFileEntitiesByLocation(Location.PC), getFileEntitiesByLocation(Location.PHONE));
    }

    public List<FileEntity> onlyOnPhone() {
        return subtract(getFileEntitiesByLocation(Location.PHONE), getFileEntitiesByLocation(Location.PC));
    }

    private List<FileEntity> subtract(List<FileEntity> list1, List<FileEntity> list2) {
        List<FileEntity> diff = new ArrayList<>(list1);
        return diff.stream()
                .filter(fileEntity -> !list2.contains(fileEntity))
                .collect(Collectors.toList());
    }

    public List<FileEntity> getFileEntitiesByLocation(Location location) {
        return fileEntityRepository.findAllByLocation(location);
    }
}

