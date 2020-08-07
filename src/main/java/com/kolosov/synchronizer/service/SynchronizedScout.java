package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.dto.ExtensionStat;
import com.kolosov.synchronizer.repository.TreeSyncRepository;
import com.kolosov.synchronizer.utils.SyncUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SynchronizedScout {

    private final TreeSyncRepository treeSyncRepository;

    public TreeSync findNotSynchronizedSyncs() {
        TreeSync treeSync = treeSyncRepository.findTree();
        //TODO удалить?
        rememberChildQuantity(treeSync);
        removeSynchronizedFiles(treeSync);
        removeEmptyFolders(treeSync);
        return treeSync;
    }

    private void rememberChildQuantity(TreeSync treeSync) {
        treeSync.getNestedSyncs()
                .filter(Sync::isFolder)
                .map(Sync::asFolder)
                .forEach(folderSync -> folderSync.rememberedChildQuantity = folderSync.list.size());
    }

    private void removeSynchronizedFiles(TreeSync treeSync) {
        treeSync.getNestedSyncs()
                .filter(Sync::isFile)
                .map(Sync::asFile)
                .filter(FileSync::isSynchronized)
                .forEach(FileSync::removeFromParent);
    }

    private void removeEmptyFolders(TreeSync treeSync) {
        List<FolderSync> folders = SyncUtils.getEmptyFolders(treeSync);
        while (!folders.isEmpty()) {
            folders.forEach(Sync::removeFromParent);
            folders = SyncUtils.getEmptyFolders(treeSync);
        }
    }

    public List<ExtensionStat> getExtensionStats() {
        return treeSyncRepository.findTree().getNestedSyncs()
                .filter(Sync::isFile)
                .map(Sync::asFile)
                .collect(Collectors.groupingBy(sync -> sync.ext, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> new ExtensionStat(entry.getKey(), entry.getValue().size(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public boolean isAllSyncsSynchronized() {
        return findNotSynchronizedSyncs().isEmpty();
    }
}
