package com.kolosov.synchronizer.utils;

import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.FolderSync;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MergeSyncsUtils {

    public static List<RootFolderSync> mergeSyncs(List<RootFolderSync> ftpFiles, List<RootFolderSync> pcFiles) {
        List<RootFolderSync> result = new ArrayList<>(pcFiles);
        List<Sync> flatFtpSyncs = SyncUtils.getFlatSyncs(ftpFiles);
        for (Sync syncToMerge : flatFtpSyncs) {
            mergeOneSync(result, syncToMerge);
        }
        return result;
    }

    private static void mergeOneSync(List<RootFolderSync> result, Sync syncToMerge) {
        Optional<RootFolderSync> rootFolderSyncOpt = findRootFolderSync(result, syncToMerge);
        rootFolderSyncOpt.ifPresentOrElse(
                rootFolderSync -> mergeSyncWithBranch(rootFolderSync, syncToMerge),
                () -> createNewRootFolder(result, syncToMerge));
    }

    private static Optional<RootFolderSync> findRootFolderSync(List<RootFolderSync> result, Sync syncToMerge) {
        String rootRelativePath = SyncUtils.getRootFolder(syncToMerge).relativePath;
        return result.stream()
                .filter(folder -> rootRelativePath.equals(folder.relativePath))
                .findFirst();
    }

    private static void createNewRootFolder(List<RootFolderSync> result, Sync syncToMerge) {
        result.add(syncToMerge.asRootFolder());
    }

    private static void mergeSyncWithBranch(FolderSync folderSync, Sync syncToMerge) {
        Optional<Sync> syncInTreeOpt = findSyncInBranch(folderSync, syncToMerge);
        syncInTreeOpt.ifPresentOrElse(
                syncInTree -> syncInTree.setExistOnPhone(true),
                () -> createNewSyncInBranch(syncToMerge, folderSync)
        );
    }

    private static void createNewSyncInBranch(Sync syncToMerge, FolderSync folderSync) {
        Optional<Sync> parentOpt = findSyncInBranch(folderSync, syncToMerge.parent);
        Sync parent = parentOpt.orElseThrow();
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
                Optional<Sync> syncInFolder = findSyncInBranch(sync.asFolder(), desiredSync);
                if (syncInFolder.isPresent()) {
                    return syncInFolder;
                }
            }
        }
        return Optional.empty();
    }

}
