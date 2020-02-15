package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.repository.FolderSyncRepository;
import com.kolosov.synchronizer.repository.TreeSyncRepository;
import com.kolosov.synchronizer.utils.SyncUtils;
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
    private final FolderSyncRepository folderSyncRepository;
    private final TreeSyncRepository treeSyncRepository;

    /*
         Работа с расширениями файлов
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
         return fileEntities.stream()
                 .filter(AbstractSync::getIsFile)
                 .collect(Collectors.groupingBy(AbstractSync::getExt, Collectors.toList()));
         throw new RuntimeException("not implemented");
     }

     public void deleteExtAll(Location location, String ext) {
         List<AbstractSync> fileEntitiesWithExt = getFileEntitiesWithExt(location, ext);
         deleteFileEntities(fileEntitiesWithExt);
     }*/
    public void deleteById(Long id) {
        //TODO Создать свой эксепшен?
        AbstractSync abstractSyncToDelete = folderSyncRepository.findById(id).orElseThrow(RuntimeException::new);
        deleteSync(abstractSyncToDelete);
    }

    private void deleteSync(AbstractSync abstractSync) {
        directOperations.deleteFile(abstractSync);
        TreeSync treeSync = getTreeSync();
        Optional<AbstractSync> syncFromDB = SyncUtils.getAbstractSyncFromTree(abstractSync, treeSync);
        syncFromDB.ifPresentOrElse(sync -> {
                    sync.parent.list.remove(sync);
                    directOperations.deleteFile(sync);
                },
                () -> {
                    throw new RuntimeException("Not Found In Tree");
                }
        );
        treeSyncRepository.save(treeSync);
    }

    public List<FolderSync> getEmptyFolders() {
        return SyncUtils.getEmptyFolders(getTreeSync());
    }

    public void deleteEmptyFolders() {
        deleteSyncs(getEmptyFolders());
    }

    private void deleteSyncs(List<? extends AbstractSync> syncs) {
        for (AbstractSync sync : syncs) {
            deleteSync(sync);
        }

        //TODO clean the tree
    }

    public void refresh() {
        log.info("refresh start");
        treeSyncRepository.deleteAll();
        createTreeSync();
        log.info("refresh done");
    }

    private void createTreeSync() {
        List<FolderSync> mergedList = directOperations.getMergedList();
        TreeSync treeSync = new TreeSync(mergedList);
        treeSyncRepository.save(treeSync);
    }

    /*public List<AbstractSync> onlyOnLocation(Location location) {
        List<Location> locations = new ArrayList<>(Arrays.asList(Location.values()));
        locations.remove(location);
        Location anotherLocation = locations.get(0);
        return subtract(getSyncs(location), getSyncs(anotherLocation));
    }*/

    private static List<AbstractSync> subtract(List<AbstractSync> list1, List<AbstractSync> list2) {
        List<AbstractSync> diff = new ArrayList<>(list1);
        return diff.stream()
                .filter(fileEntity -> !list2.contains(fileEntity))
                .collect(Collectors.toList());
    }

    public TreeSync getTreeSync() {
        return treeSyncRepository.findAll().get(0);
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

