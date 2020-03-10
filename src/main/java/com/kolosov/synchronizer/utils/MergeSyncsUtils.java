package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.FolderSync;

import java.util.List;
import java.util.Optional;

public class MergeSyncsUtils {

    public static void mergeSyncs(List<FolderSync> ftpFiles, List<FolderSync> result) {
        List<AbstractSync> flatList = SyncUtils.getFlatSyncs(ftpFiles);
        for (AbstractSync syncToMerge : flatList) {
            mergeOneSync(result, syncToMerge);
        }
    }

    private static void mergeOneSync(List<FolderSync> result, AbstractSync syncToMerge) {
        Optional<FolderSync> root = findRootFolder(result, syncToMerge);
        root.ifPresentOrElse(
                rootFolder -> mergeSyncWithBranch(syncToMerge, rootFolder),
                () -> createNewRootFolder(result, syncToMerge));
    }

    private static Optional<FolderSync> findRootFolder(List<FolderSync> result, AbstractSync syncToMerge) {
        String rootRelativePath = SyncUtils.getRootFolder(syncToMerge).relativePath;
        return result.stream()
                .filter(folder -> rootRelativePath.equals(folder.relativePath))
                .findFirst();
    }

    private static void createNewRootFolder(List<FolderSync> result, AbstractSync syncToMerge) {
        result.add(syncToMerge.asFolder());
    }

    private static void mergeSyncWithBranch(AbstractSync syncToMerge, FolderSync folderSync) {
        Optional<AbstractSync> syncInTreeOpt = findSyncInBranch(folderSync, syncToMerge);
        syncInTreeOpt.ifPresentOrElse(
                syncInTree -> syncInTree.setExistOnPhone(true),
                () -> createNewSyncInBranch(syncToMerge, folderSync)
        );
    }

    private static void createNewSyncInBranch(AbstractSync syncToMerge, FolderSync folderSync) {
        Optional<AbstractSync> parentOpt = findSyncInBranch(folderSync, syncToMerge.parent);
        AbstractSync parent = parentOpt.orElseThrow(() -> new RuntimeException("Problems with merge Syncs"));
        parent.asFolder().add(syncToMerge);
    }

    private static Optional<AbstractSync> findSyncInBranch(FolderSync syncInTree, AbstractSync desiredSync) {
        if (syncInTree.equals(desiredSync)) {
            return Optional.of(syncInTree);
        }
        for (AbstractSync sync : syncInTree.list) {
            if (sync.equals(desiredSync)) {
                return Optional.of(sync);
            }
            if (sync.isFolder()) {
                Optional<AbstractSync> syncInFolder = findSyncInBranch((FolderSync) sync, desiredSync);
                if (syncInFolder.isPresent()) {
                    return syncInFolder;
                }
            }
        }
        return Optional.empty();
    }

}
