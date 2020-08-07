package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.TreeSync;

import java.util.Optional;

public class MergeSyncsUtils {

    public static TreeSync mergeSyncs(TreeSync resultTree, TreeSync treeToMerge) {
        treeToMerge.getNestedSyncs()
                .forEach(syncToMerge -> mergeSyncWithFolder(resultTree, syncToMerge));
        return resultTree;
    }

    private static void mergeSyncWithFolder(FolderSync folderSync, Sync syncToMerge) {
        Optional<Sync> syncInTreeOpt = SyncUtils.findSyncInFolder(folderSync, syncToMerge);
        syncInTreeOpt.ifPresentOrElse(
                syncInTree -> syncInTree.setExistOnPhone(true),
                () -> createNewSyncInFolder(folderSync, syncToMerge)
        );
    }

    private static void createNewSyncInFolder(FolderSync folderSync, Sync syncToMerge) {
        Optional<Sync> parentOpt = SyncUtils.findSyncInFolder(folderSync, syncToMerge.parent);
        Sync parent = parentOpt.orElseThrow(() -> new RuntimeException("Не найден родитель для " + syncToMerge.relativePath));
        parent.asFolder().add(syncToMerge);
    }
}
