package com.kolosov.synchronizer.service.lowLevel.pc;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.service.LocationService;
import com.kolosov.synchronizer.service.lowLevel.LowLevelWorker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.kolosov.synchronizer.enums.Location.PC;

@Service
@RequiredArgsConstructor
public class PcWorker implements LowLevelWorker {

    private final LocationService locationService;

    @Override
    public List<RootFolderSync> collectSyncs() {
        List<RootFolderSync> syncList = new ArrayList<>();
        List<String> folders = locationService.getAbsolutePathsForPcFolders();
        for (String folder : folders) {
            File root = new File(folder);
            processDirectoryRecursively(root, syncList, null);
        }
        return syncList;
    }

    public void processDirectoryRecursively(File parentDir, List<RootFolderSync> rootFolderSyncs, FolderSync parentFolderSync) {
        for (final File file : parentDir.listFiles()) {
            String relativePath = locationService.relativizePcPath(file);
            String fileName = file.getName();
            if (file.isDirectory()) {
                FolderSync currentFolderSync;
                if (parentFolderSync == null) {
                    currentFolderSync = new RootFolderSync(relativePath, fileName, PC);
                    rootFolderSyncs.add(currentFolderSync.asRootFolder());
                } else {
                    currentFolderSync = new FolderSync(relativePath, fileName, PC, parentFolderSync);
                }
                processDirectoryRecursively(file, rootFolderSyncs, currentFolderSync);
            } else {
                new FileSync(relativePath, fileName, PC, parentFolderSync);
            }
        }
    }

    @Override
    @SneakyThrows
    public void delete(Sync sync) {
        String absolutePath = locationService.getAbsolutePathForPc(sync);
        FileSystemUtils.deleteRecursively(Path.of(absolutePath));
    }

    @Override
    @SneakyThrows
    public InputStream getInputStreamFrom(FileSync sync) {
        String absolutePath = locationService.getAbsolutePathForPc(sync);
        return new FileInputStream(absolutePath);
    }

    @Override
    @SneakyThrows
    public OutputStream getOutputStreamTo(FileSync sync) {
        String absolutePath = locationService.getAbsolutePathForPc(sync);
        File file = new File(absolutePath);
        file.getParentFile().mkdirs();
        return new FileOutputStream(file);
    }

    @Override
    public void createFolder(FolderSync folderSync) {
        new File(locationService.getAbsolutePathForPc(folderSync)).mkdir();
    }

}
