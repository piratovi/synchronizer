package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.repository.RootFolderSyncRepository;
import com.kolosov.synchronizer.utils.SyncUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Scout {

    private final RootFolderSyncRepository rootFolderSyncRepository;

    public List<RootFolderSync> findNotSynchronizedSyncs() {
        List<RootFolderSync> rootFolders = rootFolderSyncRepository.findAll();
        List<Sync> flatSyncs = SyncUtils.getFlatSyncs(rootFolders);
        removeSynchronizedFiles(flatSyncs);
        removeEmptyFoldersInList(rootFolders);
        return rootFolders;
    }

    private void removeSynchronizedFiles(List<Sync> flatSyncs) {
        flatSyncs.stream()
                .filter(Sync::isFile)
                .map(Sync::asFile)
                .filter(FileSync::isSynchronized)
                .forEach(FileSync::removeFromParent);
    }

    private void removeEmptyFoldersInList(List<RootFolderSync> rootFolders) {
        List<FolderSync> folders = SyncUtils.getEmptyFolders(rootFolders);
        while (!folders.isEmpty()) {
            removeEmptyFoldersInList(rootFolders, folders);
            folders = SyncUtils.getEmptyFolders(rootFolders);
        }
    }

    private void removeEmptyFoldersInList(List<RootFolderSync> rootFolders, List<FolderSync> folders) {
        folders.forEach(folderSync -> {
            if (folderSync.isRootFolder()) {
                rootFolders.remove(folderSync);
            } else {
                folderSync.removeFromParent();
            }
        });
    }

}
