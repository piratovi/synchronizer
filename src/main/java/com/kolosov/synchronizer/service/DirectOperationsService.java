package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.service.lowLevel.FtpWorker;
import com.kolosov.synchronizer.service.lowLevel.LowLevelWorker;
import com.kolosov.synchronizer.service.lowLevel.PcWorker;
import lombok.RequiredArgsConstructor;
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


    public List<FolderSync> getFileEntitiesByLocation(Location location) {
        if (location.equals(Location.PC)) {
            return getFileEntities(pcWorker.collectSyncs(), Location.PC);
        } else {
            return getFileEntities(ftpWorker.collectSyncs(), Location.PHONE);
        }
    }

    private List<FolderSync> getFileEntities(List<FolderSync> fileRelativePaths, Location location) {
//        return fileRelativePaths.stream()
//                .map(s -> {
//                    String relativePath = s.getFirst();
//                    Boolean isFile = s.getSecond();
//                    String ext = null;
//                    if (isFile) {
//                        ext = FilenameUtils.getExtension(relativePath).toLowerCase();
//                    }
//                    return new AbstractSync(relativePath, ext, location);
//                })
//                .collect(Collectors.toList());
        return null;
    }

    public void deleteFile(AbstractSync abstractSync) {
//        LowLevelWorker worker = getWorkerByLocation(abstractSync.location);
//        worker.deleteFile(abstractSync);
        return;
    }

    private LowLevelWorker getWorkerByLocation(Location location) {
        if (Location.PC.equals(location)) {
            return pcWorker;
        }
        return ftpWorker;
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

        List<AbstractSync> flatList = getFlatList(ftpFiles);
        for (AbstractSync sync : flatList) {
            mergeFileWithTree(result, sync);
        }
        return result;
    }

    private List<AbstractSync> getFlatList(List<FolderSync> ftpFiles) {
        List<AbstractSync> result = new ArrayList<>();
        ftpFiles.forEach(sync -> {
            result.add(sync);
            getListFromFileRecursively(sync, result);
        });
        return result;
    }

    private void getListFromFileRecursively(FolderSync sync, List<AbstractSync> result) {
        sync.list.forEach(syncChild -> {
            result.add(syncChild);
            if (syncChild instanceof FolderSync) {
                getListFromFileRecursively((FolderSync) syncChild, result);
            }
        });
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



