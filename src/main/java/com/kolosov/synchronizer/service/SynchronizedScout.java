package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.dto.ExtensionStat;
import com.kolosov.synchronizer.utils.SyncUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SynchronizedScout {

    private final TreeService treeService;
    private final EntityManager entityManager;

    public TreeSync findNotSynchronizedSyncs() {
        TreeSync treeSync = treeService.getTreeSync();
        entityManager.detach(treeSync);
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

    public boolean isTreeSyncNotSynchronized() {
        return treeService.getTreeSync().getNestedSyncs()
                .anyMatch(Sync::isNotSynchronized);
    }
}
