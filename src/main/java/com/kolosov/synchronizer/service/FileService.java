package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.domain.FileEntity.Location;
import com.kolosov.synchronizer.repository.FileEntityRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kolosov.synchronizer.service.DirectFileOperationsService.PATH_TO_MUSIC_PC;

@Service
@Data
@Slf4j
public class FileService {

    private final DirectFileOperationsService directOperationsService;
    private final FileEntityRepository fileEntityRepository;

    public List<FileEntity> fileEntitiesOnPC;
    public List<FileEntity> fileEntitiesOnPhone;

    @PostConstruct
    public void postConstruct() {
        log.info("PostConstruct start");
        initPC();
        initPhone();
        log.info("PostConstruct end");
    }

    private void initPC() {
        if (!PATH_TO_MUSIC_PC.toFile().exists()) {
            throw new RuntimeException("PC directory doesn't exist");
        }
        log.info("InitPC start");
        List<FileEntity> fileEntitiesOnPcFromDB = fileEntityRepository.findAllByLocation(FileEntity.Location.PC);
        if (fileEntitiesOnPcFromDB.isEmpty()) {
            this.fileEntitiesOnPC = getFileEntitiesFromPC();
        } else {
            this.fileEntitiesOnPC = fileEntitiesOnPcFromDB;
        }
        log.info("InitPC end");
    }

    private void initPhone() {
        log.info("InitPhone start");
        List<FileEntity> fileEntitiesOnPhoneFromDB = fileEntityRepository.findAllByLocation(FileEntity.Location.PHONE);
        if (fileEntitiesOnPhoneFromDB.isEmpty()) {
            this.fileEntitiesOnPhone = getFileEntitiesFromPhone();
        } else {
            this.fileEntitiesOnPhone = fileEntitiesOnPhoneFromDB;
        }
        log.info("InitPhone end");
    }

    private List<FileEntity> getFileEntitiesFromPC() {
        return directOperationsService.findFilesFromPC().stream()
                .map(FileEntity::new)
                .map(fileEntityRepository::save)
                .collect(Collectors.toList());
    }

    private List<FileEntity> getFileEntitiesFromPhone() {
        return directOperationsService.findFilesFromPhone().stream()
                .map(FileEntity::new)
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
        FileEntity deleteItem = fileEntityRepository.findById(id).orElseThrow(RuntimeException::new);
        deleteFileEntity(deleteItem);
    }

    public void deleteExtAll(Location location, String ext) {
        List<FileEntity> fileEntitiesWithExt = getFileEntitiesWithExt(location, ext);
        deleteFileEntities(fileEntitiesWithExt);
    }

    private void deleteFileEntity(FileEntity fileEntity) {
        directOperationsService.deleteFile(fileEntity);
        switch (fileEntity.location) {
            case PC: {
                fileEntitiesOnPC.remove(fileEntity);
                break;
            }
            case PHONE: {
                fileEntitiesOnPhone.remove(fileEntity);
                break;
            }
        }

    }

    public List<FileEntity> getEmptyFolders() {
        return fileEntitiesOnPC.stream()
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

    private void deleteFileEntities(List<FileEntity> fileEntities) {
        for (FileEntity fileEntity : fileEntities) {
            deleteFileEntity(fileEntity);
        }
    }

    public void refresh() {
        log.info("refresh start");
        fileEntityRepository.deleteAll();
        this.fileEntitiesOnPC = getFileEntitiesFromPC();
        log.info("refresh done");
    }

    public List<FileEntity> onlyOnPC() {
        return subtract(fileEntitiesOnPC, fileEntitiesOnPhone);
    }

    public List<FileEntity> onlyOnPhone() {
        return subtract(fileEntitiesOnPhone, fileEntitiesOnPC);
    }

    private List<FileEntity> subtract(List<FileEntity> list1, List<FileEntity> list2) {
        List<FileEntity> diff = new ArrayList<>(list1);
        return diff.stream()
                .filter(fileEntity -> !list2.contains(fileEntity))
                .collect(Collectors.toList());
    }

    public List<FileEntity> getFileEntitiesByLocation(Location location) {
        switch (location) {
            case PC: {
                return fileEntitiesOnPC;
            }
            case PHONE: {
                return fileEntitiesOnPhone;
            }
            default: {
                throw new IllegalStateException("Unexpected value: " + location);
            }
        }
    }
}
