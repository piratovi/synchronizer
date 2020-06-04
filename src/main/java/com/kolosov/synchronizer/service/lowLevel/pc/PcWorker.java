package com.kolosov.synchronizer.service.lowLevel.pc;

import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.service.lowLevel.LowLevelWorker;
import com.kolosov.synchronizer.utils.LocationUtils;
import com.kolosov.synchronizer.utils.LowLevelUtils;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.kolosov.synchronizer.utils.LocationUtils.PATH;

@Service
public class PcWorker implements LowLevelWorker {

    @Override
    public List<RootFolderSync> collectSyncs() {
        List<RootFolderSync> syncList = new ArrayList<>();
        File root = new File(LocationUtils.getPcRootPath());
        processDirectoryRecursively(root, syncList, null);
        return syncList;
    }

    public void processDirectoryRecursively(File parentDir, List<RootFolderSync> result, FolderSync parentFolderSync) {
        for (final File file : parentDir.listFiles()) {
            String relativePath = PATH.relativize(file.toPath()).toString();
            String name = file.getName();
            if (file.isDirectory()) {
                FolderSync current;
                if (parentFolderSync == null) {
                    current = new RootFolderSync(relativePath, name, Location.PC);
                    result.add(current.asRootFolder());
                } else {
                    current = new FolderSync(relativePath, name, Location.PC, parentFolderSync);
                    parentFolderSync.list.add(current);
                }
                processDirectoryRecursively(file, result, current);
            }
            if (file.isFile()) {
                FileSync current = new FileSync(relativePath, name, Location.PC, parentFolderSync);
                parentFolderSync.list.add(current);
            }
        }
    }

    @Override
    @SneakyThrows
    public void delete(Sync sync) {
        FileSystemUtils.deleteRecursively(Path.of(LowLevelUtils.getAbsolutePath(sync)));
    }

    @Override
    @SneakyThrows
    public InputStream getInputStreamFrom(Sync sync) {
        FileInputStream fileInputStream;
        fileInputStream = new FileInputStream(LowLevelUtils.getAbsolutePath(sync));
        return fileInputStream;
    }

    @Override
    @SneakyThrows
    public OutputStream getOutputStreamTo(Sync sync) {
        FileOutputStream outputStream;
        String absolutePath = LowLevelUtils.getAbsolutePath(sync);
        File file = new File(absolutePath);
        file.getParentFile().mkdirs();
        outputStream = new FileOutputStream(file);
        return outputStream;
    }

    @Override
    public void createFolder(FolderSync folderSync) {
        new File(LowLevelUtils.getAbsolutePath(folderSync)).mkdir();
    }

}
