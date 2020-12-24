package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.TreeSync;

import java.util.Optional;

public class MergeSyncUtils {

    public static TreeSync mergeTrees(TreeSync resultTree, TreeSync treeToMerge) {
        treeToMerge.list.forEach(syncToMerge -> mergeSyncWithTree(resultTree, syncToMerge));
        return resultTree;
    }

    static void mergeSyncWithTree(TreeSync treeSync, Sync syncToMerge) {
        Optional<Sync> syncInTreeOpt = SyncUtils.findSync(treeSync, syncToMerge);
        syncInTreeOpt.ifPresentOrElse(
                sync -> {
                    sync.setSynchronized();
                    if (syncToMerge.isFolder()) {
                        syncToMerge.asFolder().list.forEach(nestedSync -> {
                            mergeSyncWithTree(treeSync, nestedSync);
                        });
                    }
                },
                () -> addSyncToParentFolder(treeSync, syncToMerge)
        );
    }

    static void addSyncToParentFolder(FolderSync folderSync, Sync syncToMerge) {
        Optional<Sync> parentOpt = SyncUtils.findSync(folderSync, syncToMerge.parent);
        Sync parent = parentOpt.orElseThrow(() -> new RuntimeException("Не найден родитель для " + syncToMerge.relativePath));
        parent.asFolder().add(syncToMerge);
    }
}
