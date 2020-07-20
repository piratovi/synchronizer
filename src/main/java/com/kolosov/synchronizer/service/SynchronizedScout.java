package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.dto.ExtensionStat;
import com.kolosov.synchronizer.repository.RootFolderSyncRepository;
import com.kolosov.synchronizer.utils.SyncUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SynchronizedScout {

    private final RootFolderSyncRepository rootFolderSyncRepository;

    public List<RootFolderSync> findNotSynchronizedSyncs() {
        List<RootFolderSync> rootFolders = rootFolderSyncRepository.findAll();
        List<Sync> flatSyncs = SyncUtils.getFlatSyncs(rootFolders);
        rememberChildQuantity(flatSyncs);
        removeSynchronizedFiles(flatSyncs);
        removeEmptyFoldersInList(rootFolders);
        return rootFolders;
    }

    private void rememberChildQuantity(List<Sync> flatSyncs) {
        flatSyncs.stream()
                .filter(Sync::isFolder)
                .map(Sync::asFolder)
                .forEach(folderSync -> folderSync.rememberedChildQuantity = folderSync.list.size());
    }

    private void removeSynchronizedFiles(List<Sync> flatSyncs) {
        flatSyncs.stream()
                .filter(Sync::isFile)
                .map(Sync::asFile)
                .filter(FileSync::isSynchronized)
                .forEach(FileSync::removeFromParent);
    }

    private void removeEmptyFoldersInList(List<RootFolderSync> rootFolders) {
        List<FolderSync> folders = SyncUtils.getEmptyFolders(rootFolders);
        while (!folders.isEmpty()) {
            removeEmptyFoldersInList(rootFolders, folders);
            folders = SyncUtils.getEmptyFolders(rootFolders);
        }
    }

    private void removeEmptyFoldersInList(List<RootFolderSync> rootFolders, List<FolderSync> folders) {
        folders.forEach(folderSync -> {
            if (folderSync.isRootFolder()) {
                rootFolders.remove(folderSync);
            } else {
                folderSync.removeFromParent();
            }
        });
    }

    public List<ExtensionStat> getExtensionStats() {
        List<Sync> flatSyncs = SyncUtils.getFlatSyncs(rootFolderSyncRepository.findAll());
        return flatSyncs.stream()
                .filter(sync -> sync instanceof FileSync)
                .map(sync -> (FileSync) sync)
                .collect(Collectors.groupingBy(sync -> sync.ext, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> new ExtensionStat(entry.getKey(), entry.getValue().size(), entry.getValue()))
                .collect(Collectors.toList());
    }

}
