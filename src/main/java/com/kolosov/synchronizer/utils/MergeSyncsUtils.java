package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.FolderSync;

import java.util.List;
import java.util.Optional;

public class MergeSyncsUtils {

    public static void mergeSyncs(List<RootFolderSync> ftpFiles, List<RootFolderSync> result) {
        List<Sync> flatList = SyncUtils.getFlatSyncs(ftpFiles);
        for (Sync syncToMerge : flatList) {
            mergeOneSync(result, syncToMerge);
        }
    }

    private static void mergeOneSync(List<RootFolderSync> result, Sync syncToMerge) {
        Optional<? extends FolderSync> root = findRootFolder(result, syncToMerge);
        root.ifPresentOrElse(
                rootFolder -> mergeSyncWithBranch(syncToMerge, rootFolder),
                () -> createNewRootFolder(result, syncToMerge));
    }

    private static Optional<RootFolderSync> findRootFolder(List<RootFolderSync> result, Sync syncToMerge) {
        String rootRelativePath = SyncUtils.getRootFolder(syncToMerge).relativePath;
        return result.stream()
                .filter(folder -> rootRelativePath.equals(folder.relativePath))
                .findFirst();
    }

    private static void createNewRootFolder(List<RootFolderSync> result, Sync syncToMerge) {
        result.add(syncToMerge.asRootFolder());
    }

    private static void mergeSyncWithBranch(Sync syncToMerge, FolderSync folderSync) {
        Optional<Sync> syncInTreeOpt = findSyncInBranch(folderSync, syncToMerge);
        syncInTreeOpt.ifPresentOrElse(
                syncInTree -> syncInTree.setExistOnPhone(true),
                () -> createNewSyncInBranch(syncToMerge, folderSync)
        );
    }

    private static void createNewSyncInBranch(Sync syncToMerge, FolderSync folderSync) {
        Optional<Sync> parentOpt = findSyncInBranch(folderSync, syncToMerge.parent);
        Sync parent = parentOpt.orElseThrow(() -> new RuntimeException("Problems with merge Syncs"));
        parent.asFolder().add(syncToMerge);
    }

    private static Optional<Sync> findSyncInBranch(FolderSync syncInTree, Sync desiredSync) {
        if (syncInTree.equals(desiredSync)) {
            return Optional.of(syncInTree);
        }
        for (Sync sync : syncInTree.list) {
            if (sync.equals(desiredSync)) {
                return Optional.of(sync);
            }
            if (sync.isFolder()) {
                Optional<Sync> syncInFolder = findSyncInBranch((FolderSync) sync, desiredSync);
                if (syncInFolder.isPresent()) {
                    return syncInFolder;
                }
            }
        }
        return Optional.empty();
    }

}
