package com.kolosov.synchronizer.service.lowLevel.pc;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.service.lowLevel.LowLevelWorker;
import com.kolosov.synchronizer.service.LocationService;
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

    public void processDirectoryRecursively(File parentDir, List<RootFolderSync> result, FolderSync parentFolderSync) {
        for (final File file : parentDir.listFiles()) {
            String relativePath = locationService.relativizePcPath(file);
            String name = file.getName();
            if (file.isDirectory()) {
                FolderSync current;
                if (parentFolderSync == null) {
                    current = createRootFolderSync(result, relativePath, name);
                } else {
                    current = createFolderSync(parentFolderSync, relativePath, name);
                }
                processDirectoryRecursively(file, result, current);
            }
            if (file.isFile()) {
                createFileSync(parentFolderSync, relativePath, name);
            }
        }
    }

    private FolderSync createFolderSync(FolderSync parentFolderSync, String relativePath, String name) {
        FolderSync current = new FolderSync(relativePath, name, Location.PC, parentFolderSync);
        parentFolderSync.list.add(current);
        return current;
    }

    private FolderSync createRootFolderSync(List<RootFolderSync> result, String relativePath, String name) {
        FolderSync current = new RootFolderSync(relativePath, name, Location.PC);
        result.add(current.asRootFolder());
        return current;
    }

    private void createFileSync(FolderSync parentFolderSync, String relativePath, String name) {
        FileSync current = new FileSync(relativePath, name, Location.PC, parentFolderSync);
        parentFolderSync.list.add(current);
    }

    @Override
    @SneakyThrows
    public void delete(Sync sync) {
        FileSystemUtils.deleteRecursively(Path.of(locationService.getAbsolutePathForPc(sync)));
    }

    @Override
    @SneakyThrows
    public InputStream getInputStreamFrom(FileSync sync) {
        FileInputStream fileInputStream;
        fileInputStream = new FileInputStream(locationService.getAbsolutePathForPc(sync));
        return fileInputStream;
    }

    @Override
    @SneakyThrows
    public OutputStream getOutputStreamTo(FileSync sync) {
        FileOutputStream outputStream;
        String absolutePath = locationService.getAbsolutePathForPc(sync);
        File file = new File(absolutePath);
        file.getParentFile().mkdirs();
        outputStream = new FileOutputStream(file);
        return outputStream;
    }

    @Override
    public void createFolder(FolderSync folderSync) {
        new File(locationService.getAbsolutePathForPc(folderSync)).mkdir();
    }

}
