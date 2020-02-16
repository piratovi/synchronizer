package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.exceptions.FileNotFoundException;
import com.kolosov.synchronizer.service.lowLevel.FtpWorker;
import com.kolosov.synchronizer.service.lowLevel.LowLevelWorker;
import com.kolosov.synchronizer.service.lowLevel.PcWorker;
import com.kolosov.synchronizer.utils.SyncUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectOperationsService {

    private final FtpWorker ftpWorker;
    private final PcWorker pcWorker;

    public void deleteFile(AbstractSync sync) {
        if (!sync.existOnPhone && !sync.existOnPC) {
            throw new FileNotFoundException("File already deleted " + sync.relativePath);
        }
        if (sync.existOnPhone) {
            ftpWorker.deleteFile(sync);
        }
        if (sync.existOnPC) {
            pcWorker.deleteFile(sync);
        }
    }

    public void copyFileFromPhoneToPc(AbstractSync abstractSync) {
        try (
                InputStream inputStream = ftpWorker.getInputStreamFromFile(abstractSync);
                OutputStream outputStream = pcWorker.getOutputStreamToFile(abstractSync)
        ) {
            inputStream.transferTo(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ftpWorker.closeStream();

    }

    public void copyFileFromPcToPhone(AbstractSync abstractSync) {
        try (
                InputStream inputStream = pcWorker.getInputStreamFromFile(abstractSync);
                OutputStream outputStream = ftpWorker.getOutputStreamToFile(abstractSync)
        ) {
            inputStream.transferTo(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ftpWorker.closeStream();
    }

    public List<FolderSync> getMergedList() {
        List<FolderSync> pcFiles = pcWorker.collectSyncs();
        List<FolderSync> ftpFiles = ftpWorker.collectSyncs();
        List<FolderSync> result = new ArrayList<>(pcFiles);

        List<AbstractSync> flatList = SyncUtils.getFlatSyncs(ftpFiles);
        for (AbstractSync sync : flatList) {
            mergeFileWithTree(result, sync);
        }
        SyncUtils.processExtensions(result);
        return result;
    }

    private void mergeFileWithTree(List<FolderSync> result, AbstractSync sync) {
        Optional<FolderSync> root = result.stream()
                .filter(folder -> sync.relativePath.startsWith(folder.relativePath))
                .findFirst();

        root.ifPresentOrElse(folderSync -> {
                    Optional<AbstractSync> desiredOpt = findSyncInFolderBranch(folderSync, sync);
                    desiredOpt.ifPresentOrElse(
                            desired -> desired.setExistOnPhone(true)
                            , () -> {
                                Optional<AbstractSync> parentOpt = findSyncInFolderBranch(folderSync, sync.parent);
                                parentOpt.ifPresentOrElse(parent -> {
                                            FolderSync parentAsFolder = (FolderSync) parent;
                                            parentAsFolder.list.add(sync);
                                            sync.parent = parentAsFolder;
                                        },
                                        () -> {
                                            if (sync.parent == null && sync instanceof FolderSync) {
                                                result.add((FolderSync) sync);
                                            } else {
                                                throw new RuntimeException("Problems with parent search");
                                            }
                                        });
                            }
                    );
                },
                () -> result.add((FolderSync) sync)
        );


    }

    private Optional<AbstractSync> findSyncInFolderBranch(FolderSync syncInTree, AbstractSync desiredSync) {
        if (syncInTree.equals(desiredSync)) {
            return Optional.of(syncInTree);
        }
        for (AbstractSync sync : syncInTree.list) {
            if (sync.equals(desiredSync)) {
                return Optional.of(sync);
            }
            if (sync instanceof FolderSync) {
                Optional<AbstractSync> syncInFolder = findSyncInFolderBranch((FolderSync) sync, desiredSync);
                if (syncInFolder.isPresent()) {
                    return syncInFolder;
                }
            }
        }
        return Optional.empty();
    }

}



