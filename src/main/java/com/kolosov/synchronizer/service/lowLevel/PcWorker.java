package com.kolosov.synchronizer.service.lowLevel;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.utils.LocationUtils;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.kolosov.synchronizer.utils.LocationUtils.PATH;

@Service
public class PcWorker implements LowLevelWorker {

    @Override
    public List<FolderSync> collectSyncs() {
        List<FolderSync> syncList = new ArrayList<>();
        File root = new File(LocationUtils.getPcRootPath());
        processDirectoryRecursively(root, syncList, null);
        return syncList;
    }

    //TODO проверка на null
    public void processDirectoryRecursively(File parentDir, List<FolderSync> result, FolderSync parentFolderSync) {
        for (final File file : parentDir.listFiles()) {
            String relativePath = PATH.relativize(file.toPath()).toString();
            String name = file.getName();
            if (file.isDirectory()) {
                FolderSync current;
                if (parentFolderSync == null) {
                    current = new FolderSync(relativePath, name, Location.PC, null);
                    result.add(current);
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
    public void deleteFile(AbstractSync sync) {
        FileSystemUtils.deleteRecursively(Path.of(Utils.getAbsolutePath(sync)));
    }

    @Override
    @SneakyThrows
    public InputStream getInputStreamFromFile(AbstractSync abstractSync) {
        FileInputStream fileInputStream;
        fileInputStream = new FileInputStream(Utils.getAbsolutePath(abstractSync));
        return fileInputStream;
    }

    @Override
    @SneakyThrows
    public OutputStream getOutputStreamToFile(AbstractSync abstractSync) {
        FileOutputStream outputStream;
        String absolutePath = Utils.getAbsolutePath(abstractSync);
        File file = new File(absolutePath);
        file.getParentFile().mkdirs();
        outputStream = new FileOutputStream(file);
        return outputStream;
    }

}
