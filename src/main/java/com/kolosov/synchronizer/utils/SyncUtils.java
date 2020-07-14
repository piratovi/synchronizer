package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SyncUtils {

    public static List<Sync> getFlatSyncs(List<? extends Sync> folderSyncs) {
        List<Sync> result = new ArrayList<>();
        folderSyncs.forEach(sync -> result.addAll(getFlatSyncs(sync)));
        return result;
    }

    public static List<Sync> getFlatSyncs(Sync sync) {
        List<Sync> result = new ArrayList<>();
        result.add(sync);
        if (sync.isFolder()) {
            getNestedSyncsRecursively(sync.asFolder(), result);
        }
        return result;
    }

    private static void getNestedSyncsRecursively(FolderSync parent, List<Sync> result) {
        parent.list.forEach(child -> {
            result.add(child);
            if (child.isFolder()) {
                getNestedSyncsRecursively(child.asFolder(), result);
            }
        });
    }

    public static RootFolderSync getRootFolder(Sync sync) {
        while (!sync.isRootFolder()) {
            sync = sync.getParent();
        }
        return sync.asRootFolder();
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

    public static List<FolderSync> getEmptyFolders(List<? extends Sync> syncs) {
        List<Sync> flatSyncs = getFlatSyncs(syncs);
        return flatSyncs.stream()
                .filter(Sync::isFolder)
                .map(Sync::asFolder)
                .filter(FolderSync::isEmpty)
                .collect(Collectors.toList());
    }
}
