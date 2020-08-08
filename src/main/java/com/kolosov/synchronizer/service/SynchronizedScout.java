package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.dto.ExtensionStat;
import com.kolosov.synchronizer.utils.SyncUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SynchronizedScout {

    private final TreeService treeService;

    public TreeSync findNotSynchronizedSyncs() {
        TreeSync treeSync = treeService.getTreeSync();
        removeSynchronizedFiles(treeSync);
        removeEmptyFolders(treeSync);
        return treeSync;
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
        return treeService.getTreeSync().getNestedSyncs()
                .filter(Sync::isFile)
                .map(Sync::asFile)
                .collect(Collectors.groupingBy(sync -> sync.ext, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> new ExtensionStat(entry.getKey(), entry.getValue().size(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public boolean isTreeSyncNotSynchronized() {
        return treeService.getTreeSync().getNestedSyncs()
                .anyMatch(Sync::isNotSynchronized);
    }
}
