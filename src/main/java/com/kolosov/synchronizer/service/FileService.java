package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.repository.FolderSyncRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final DirectOperationsService directOperations;
    private final FolderSyncRepository repository;

    private void createFileEntities(Location location) {
        List<AbstractSync> fileEntities = directOperations.getFileEntitiesByLocation(location);
//        fileEntities.forEach(repository::save);
    }

    public Set<String> getExtensions(Location location) {
        List<AbstractSync> fileEntities = getFileEntitiesByLocation(location);
        Map<String, List<AbstractSync>> fileEntitiesSeparatedByExtensions = separateFileEntitiesByExtensions(fileEntities);
        return fileEntitiesSeparatedByExtensions.keySet();
    }

    public List<AbstractSync> getFileEntitiesWithExt(Location location, String ext) {
        List<AbstractSync> fileEntities = getFileEntitiesByLocation(location);
        Map<String, List<AbstractSync>> fileEntitiesSeparatedByExtensions = separateFileEntitiesByExtensions(fileEntities);
        return fileEntitiesSeparatedByExtensions.get(ext);

    }

    private Map<String, List<AbstractSync>> separateFileEntitiesByExtensions(List<AbstractSync> fileEntities) {
//        return fileEntities.stream()
//                .filter(AbstractSync::getIsFile)
//                .collect(Collectors.groupingBy(AbstractSync::getExt, Collectors.toList()));
        throw new RuntimeException("not implemented");
    }

    public void deleteById(Long id) {
        //TODO Создать свой эксепшен?
        AbstractSync abstractSyncToDelete = repository.findById(id).orElseThrow(RuntimeException::new);
        deleteFileEntity(abstractSyncToDelete);
    }

    public void deleteExtAll(Location location, String ext) {
        List<AbstractSync> fileEntitiesWithExt = getFileEntitiesWithExt(location, ext);
        deleteFileEntities(fileEntitiesWithExt);
    }

    private void deleteFileEntity(AbstractSync abstractSync) {
        directOperations.deleteFile(abstractSync);
//        repository.delete(abstractSync);
    }

    public List<AbstractSync> getEmptyFolders(Location location) {
//        return repository.findAllByLocation(location).stream()
//                .filter(fileEntity -> !fileEntity.isFile)
//                .filter(fileEntity -> {
//                    File file = new File(fileEntity.relativePath);
//                    return Objects.requireNonNull(file.listFiles()).length == 0;
//                })
//                .collect(Collectors.toList());
        throw new RuntimeException("not implemented");
    }

    public void deleteEmptyFolders(Location location) {
        deleteFileEntities(getEmptyFolders(location));
    }

    private void deleteFileEntities(List<AbstractSync> fileEntities) {
        for (AbstractSync abstractSync : fileEntities) {
            deleteFileEntity(abstractSync);
        }
    }

    public void refresh(Location location) {
//        log.info("refresh " + location + " start");
//        repository.deleteAllByLocation(location);
//        createFileEntities(location);
//        log.info("refresh " + location + " done");
    }

    public List<AbstractSync> onlyOnLocation(Location location) {
        List<Location> locations = new ArrayList<>(Arrays.asList(Location.values()));
        locations.remove(location);
        Location anotherLocation = locations.get(0);
        return subtract(getFileEntitiesByLocation(location), getFileEntitiesByLocation(anotherLocation));
    }

    private static List<AbstractSync> subtract(List<AbstractSync> list1, List<AbstractSync> list2) {
        List<AbstractSync> diff = new ArrayList<>(list1);
        return diff.stream()
                .filter(fileEntity -> !list2.contains(fileEntity))
                .collect(Collectors.toList());
    }

    public List<AbstractSync> getFileEntitiesByLocation(Location location) {
//        return repository.findAllByLocation(location);
        return null;
    }

    public void transferFileEntity(Long id) {
//        AbstractSync abstractSync = repository.findById(id).orElseThrow(RuntimeException::new);
//        AbstractSync transferredEntity;
//        if (Location.PC.equals(abstractSync.location)) {
//            directOperations.copyFileFromPcToPhone(abstractSync);
//            transferredEntity = new AbstractSync(abstractSync.relativePath, abstractSync.ext, Location.PHONE);
//        } else {
//            directOperations.copyFileFromPhoneToPc(abstractSync);
//            transferredEntity = new AbstractSync(abstractSync.relativePath, abstractSync.ext, Location.PC);
//        }
//        repository.save(transferredEntity);
        throw new RuntimeException("not implemented");
    }

}

