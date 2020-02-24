package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.TreeSync;
import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SyncUtils {


    public static List<AbstractSync> getFlatSyncs(List<FolderSync> folderSyncs) {
        List<AbstractSync> result = new ArrayList<>();
            folderSyncs.forEach(sync -> {
                result.add(sync);
                getNestedSyncsRecursively(sync, result);
            });
        return result;
    }

    private static void getNestedSyncsRecursively(FolderSync sync, List<AbstractSync> result) {
        sync.list.forEach(syncChild -> {
            result.add(syncChild);
            if (syncChild instanceof FolderSync) {
                getNestedSyncsRecursively((FolderSync) syncChild, result);
            }
        });
    }

    public static void processExtensions(List<FolderSync> folderSyncs) {
        List<AbstractSync> flatSyncs = getFlatSyncs(folderSyncs);
        flatSyncs.stream()
                .filter(sync -> sync instanceof FileSync)
                .forEach(sync -> ((FileSync) sync).ext = FilenameUtils.getExtension(sync.relativePath).toLowerCase());
    }

    public static List<FolderSync> getEmptyFolders(TreeSync treeSync) {
        List<AbstractSync> flatSyncs = getFlatSyncs(treeSync.folderSyncs);
        return flatSyncs.stream()
                .filter(sync -> sync instanceof FolderSync)
                .map(sync -> ((FolderSync) sync))
                .filter(folderSync -> folderSync.list.isEmpty())
                .collect(Collectors.toList());
    }

    //TODO сделать нормальный обход по дереву
    public static Optional<AbstractSync> getAbstractSyncFromTree(AbstractSync desiredSync, TreeSync treeSync) {
        for (FolderSync folderSync : treeSync.folderSyncs) {
            Optional<AbstractSync> syncOpt = getAbstractSyncRecursively(folderSync, desiredSync);
            if (syncOpt.isPresent()) {
                return syncOpt;
            }
        }
        return Optional.empty();
    }

    private static Optional<AbstractSync> getAbstractSyncRecursively(FolderSync folderSync, AbstractSync desiredSync) {
        if (folderSync.equals(desiredSync)) {
            return Optional.of(folderSync);
        }
        for (AbstractSync sync : folderSync.list) {
            if (sync.equals(desiredSync)) {
                return Optional.of(sync);
            }
            if (sync instanceof FolderSync) {
                Optional<AbstractSync> syncOpt = getAbstractSyncRecursively((FolderSync) sync, desiredSync);
                if (syncOpt.isPresent()) {
                    return syncOpt;
                }
            }
        }
        return Optional.empty();
    }
}
