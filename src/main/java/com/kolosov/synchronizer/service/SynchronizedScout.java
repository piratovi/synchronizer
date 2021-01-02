package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.utils.SyncUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SynchronizedScout {

    private final TreeService treeService;
    private final EntityManager entityManager;

    public TreeSync getNotSynchronizedSyncs() {
        TreeSync treeSync = treeService.getTreeSync();
        entityManager.detach(treeSync);
        removeSynchronizedFiles(treeSync);
        List<FolderSync> foldersWithoutNestedFiles = SyncUtils.getFoldersWithoutNestedFiles(treeSync, true);
        foldersWithoutNestedFiles.forEach(Sync::removeFromParent);
        return treeSync;
    }

    private void removeSynchronizedFiles(TreeSync treeSync) {
        treeSync.getNestedSyncs()
                .filter(Sync::isFile)
                .map(Sync::asFile)
                .filter(FileSync::isSynchronized)
                .forEach(FileSync::removeFromParent);
    }

}
