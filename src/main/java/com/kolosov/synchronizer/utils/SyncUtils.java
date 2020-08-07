package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.TreeSync;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SyncUtils {

    public static Optional<Sync> findSyncInFolder(FolderSync syncInResultTree, Sync syncToMerge) {
        if (syncInResultTree.equals(syncToMerge)) {
            return Optional.of(syncInResultTree);
        }
        for (Sync sync : syncInResultTree.list) {
            if (sync.equals(syncToMerge)) {
                return Optional.of(sync);
            }
            if (sync.isFolder()) {
                Optional<Sync> syncInFolder = findSyncInFolder(sync.asFolder(), syncToMerge);
                if (syncInFolder.isPresent()) {
                    return syncInFolder;
                }
            }
        }
        return Optional.empty();
    }

    public static List<FolderSync> getParents(Sync sync) {
        List<FolderSync> parents = new ArrayList<>();
        FolderSync parent = sync.parent;
        while (parent != null) {
            parents.add(0, parent);
            parent = parent.parent;
        }
        return parents;
    }

    public static List<FolderSync> getEmptyFolders(TreeSync treeSync) {
        return treeSync.getNestedSyncs()
                .filter(Sync::isFolder)
                .map(Sync::asFolder)
                .filter(FolderSync::isEmpty)
                .collect(Collectors.toList());
    }
}
