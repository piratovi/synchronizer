package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.repository.SyncRepository;
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
    private final TreeSyncRepository treeSyncRepository;
    private final SyncRepository syncRepository;

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
        Optional<AbstractSync> syncOpt = syncRepository.findById(id);
        syncOpt.ifPresentOrElse(
                this::deleteSync,
                () -> {
                    throw new RuntimeException();
                });
    }

    private void deleteSync(AbstractSync syncToDelete) {
        directOperations.deleteFile(syncToDelete);
        cleanTree(syncToDelete);
    }

    private void cleanTree(AbstractSync syncToDelete) {
        FolderSync parentSync = syncToDelete.parent;
        if (parentSync != null) {
            parentSync.list.remove(syncToDelete);
            syncRepository.save(parentSync);
        } else {
            TreeSync treeSync = getTreeSync();
            boolean remove = treeSync.folderSyncs.remove(syncToDelete);
            if (!remove) {
                throw new RuntimeException("Error With deleting");
            }
            treeSyncRepository.save(treeSync);
            //TODO удалять и AbstractSync
        }
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
    }

    public void refresh() {
        log.info("refresh start");
        treeSyncRepository.deleteAll();
        syncRepository.deleteAll();
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

    public void transferSync(Long id) {
        AbstractSync sync = syncRepository.findById(id).orElseThrow();
        if (sync.existOnPC && sync.existOnPhone || !sync.existOnPC && !sync.existOnPhone) {
            throw new RuntimeException();
        }
        if (sync.existOnPC) {
            directOperations.copyFileFromPcToPhone(sync);
        } else {
            directOperations.copyFileFromPhoneToPc(sync);
        }
    }

}

