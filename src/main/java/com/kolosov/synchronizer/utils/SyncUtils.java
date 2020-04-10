package com.kolosov.synchronizer.utils;

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

    private static void getNestedSyncsRecursively(FolderSync sync, List<Sync> result) {
        sync.list.forEach(syncChild -> {
            result.add(syncChild);
            if (syncChild instanceof FolderSync) {
                getNestedSyncsRecursively((FolderSync) syncChild, result);
            }
        });
    }

    public static void processExtensions(List<FolderSync> folderSyncs) {
        List<Sync> flatSyncs = getFlatSyncs(folderSyncs);
        flatSyncs.stream()
                .filter(sync -> sync instanceof FileSync)
                .forEach(sync -> ((FileSync) sync).ext = FilenameUtils.getExtension(sync.relativePath).toLowerCase());
    }

    public static List<FolderSync> getEmptyFolders(List<? extends FolderSync> rootFolders) {
        List<Sync> flatSyncs = getFlatSyncs(rootFolders);
        return flatSyncs.stream()
                .filter(sync -> sync instanceof FolderSync)
                .map(sync -> ((FolderSync) sync))
                .filter(folderSync -> folderSync.list.isEmpty())
                .collect(Collectors.toList());
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
}
