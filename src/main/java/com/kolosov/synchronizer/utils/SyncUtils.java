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

    //TODO сделать нормальный обход по дереву
//    public static Optional<AbstractSync> getAbstractSyncFromTree(AbstractSync desiredSync, TreeSync treeSync) {
//        for (FolderSync folderSync : treeSync.folderSyncs) {
//            Optional<AbstractSync> syncOpt = getAbstractSyncRecursively(folderSync, desiredSync);
//            if (syncOpt.isPresent()) {
//                return syncOpt;
//            }
//        }
//        return Optional.empty();
//    }

    private static Optional<Sync> getAbstractSyncRecursively(FolderSync folderSync, Sync desiredSync) {
        if (folderSync.equals(desiredSync)) {
            return Optional.of(folderSync);
        }
        for (Sync sync : folderSync.list) {
            if (sync.equals(desiredSync)) {
                return Optional.of(sync);
            }
            if (sync instanceof FolderSync) {
                Optional<Sync> syncOpt = getAbstractSyncRecursively((FolderSync) sync, desiredSync);
                if (syncOpt.isPresent()) {
                    return syncOpt;
                }
            }
        }
        return Optional.empty();
    }

    public static FolderSync getRootFolder(Sync sync) {
        if (sync.parent == null) {
            return (FolderSync) sync;
        } else {
            return getRootFolder(sync.parent);
        }
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
