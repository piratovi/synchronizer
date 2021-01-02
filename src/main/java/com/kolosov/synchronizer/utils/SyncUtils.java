package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.TreeSync;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SyncUtils {

    public static Optional<Sync> findSync(Sync searchLocation, Sync searchTarget) {
        if (searchLocation.equals(searchTarget)) {
            return Optional.of(searchLocation);
        }
        if (searchLocation.isFolder()) {
            return searchLocation.asFolder().list.stream()
                    .filter(sync -> searchTarget.relativePath.startsWith(sync.relativePath))
                    .max(Comparator.comparingInt(o -> o.relativePath.length()))
                    .flatMap(sync -> findSync(sync, searchTarget));
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
                .filter(folderSync -> !folderSync.isTree())
                .collect(Collectors.toList());
    }

    public static List<FolderSync> getFoldersWithoutNestedFiles(TreeSync treeSync, boolean includeRootFolders) {
        return treeSync.getNestedSyncs()
                .filter(Sync::isFolder)
                .map(Sync::asFolder)
                .filter(folderSync -> folderSync.getNestedSyncs().allMatch(Sync::isFolder))
                .filter(folderSync -> includeRootFolders ? true : !folderSync.getParent().isTree())
                .collect(Collectors.toList());
    }

}
