package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.domain.Location;
import com.kolosov.synchronizer.repository.FileEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final DirectOperationsService directOperations;
    private final FileEntityRepository repository;

    private void createFileEntities(Location location) {
        List<FileEntity> fileEntities = directOperations.getFileEntitiesByLocation(location);
        fileEntities.forEach(repository::save);
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

    private Map<String, List<FileEntity>> separateFileEntitiesByExtensions(List<FileEntity> fileEntities) {
        return fileEntities.stream()
                .filter(FileEntity::getIsFile)
                .collect(Collectors.groupingBy(FileEntity::getExt, Collectors.toList()));
    }

    public void deleteById(Long id) {
        //TODO Создать свой эксепшен?
        FileEntity fileEntityToDelete = repository.findById(id).orElseThrow(RuntimeException::new);
        deleteFileEntity(fileEntityToDelete);
    }

    public void deleteExtAll(Location location, String ext) {
        List<FileEntity> fileEntitiesWithExt = getFileEntitiesWithExt(location, ext);
        deleteFileEntities(fileEntitiesWithExt);
    }

    private void deleteFileEntity(FileEntity fileEntity) {
        directOperations.deleteFile(fileEntity);
        repository.delete(fileEntity);
    }

    public List<FileEntity> getEmptyFolders(Location location) {
        return repository.findAllByLocation(location).stream()
                .filter(fileEntity -> !fileEntity.isFile)
                .filter(fileEntity -> {
                    File file = new File(fileEntity.relativePath);
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
        log.info("refresh " + location + " start");
        repository.deleteAllByLocation(location);
        createFileEntities(location);
        log.info("refresh " + location + " done");
    }

    public List<FileEntity> onlyOnLocation(Location location) {
        List<Location> locations = new ArrayList<>(Arrays.asList(Location.values()));
        locations.remove(location);
        Location anotherLocation = locations.get(0);
        return subtract(getFileEntitiesByLocation(location), getFileEntitiesByLocation(anotherLocation));
    }

    private static List<FileEntity> subtract(List<FileEntity> list1, List<FileEntity> list2) {
        List<FileEntity> diff = new ArrayList<>(list1);
        return diff.stream()
                .filter(fileEntity -> !list2.contains(fileEntity))
                .collect(Collectors.toList());
    }

    public List<FileEntity> getFileEntitiesByLocation(Location location) {
        return repository.findAllByLocation(location);
    }

    public void transferFileEntity(Long id) {
        FileEntity fileEntity = repository.findById(id).orElseThrow(RuntimeException::new);
        FileEntity transferredEntity;
        if (Location.PC.equals(fileEntity.location)) {
            directOperations.copyFileFromPcToPhone(fileEntity);
            transferredEntity = new FileEntity(fileEntity.relativePath, fileEntity.isFile, fileEntity.ext, Location.PHONE);
        } else {
            directOperations.copyFileFromPhoneToPc(fileEntity);
            transferredEntity = new FileEntity(fileEntity.relativePath, fileEntity.isFile, fileEntity.ext, Location.PC);
        }
        repository.save(transferredEntity);
    }
}

