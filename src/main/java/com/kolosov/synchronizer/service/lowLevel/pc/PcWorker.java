package com.kolosov.synchronizer.service.lowLevel.pc;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.service.LocationService;
import com.kolosov.synchronizer.service.lowLevel.LowLevelWorker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static com.kolosov.synchronizer.enums.Location.PC;

@Service
@RequiredArgsConstructor
public class PcWorker implements LowLevelWorker {

    private final LocationService locationService;

    @Override
    public TreeSync getNewTreeSync() {
        TreeSync newTreeSync = new TreeSync("\\", "\\", PC);
        List<String> folders = locationService.getAbsolutePathsForPcFolders();
        for (String folder : folders) {
            File root = new File(folder);
            processDirectoryRecursively(root, newTreeSync);
        }
        return newTreeSync;
    }

    public void processDirectoryRecursively(File parentDirectory, FolderSync parentFolderSync) {
        for (final File file : parentDirectory.listFiles()) {
            String relativePath = locationService.relativizePcPath(file);
            String fileName = file.getName();
            if (file.isDirectory()) {
                FolderSync currentFolderSync = new FolderSync(relativePath, fileName, PC, parentFolderSync);
                processDirectoryRecursively(file, currentFolderSync);
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

    @SneakyThrows
    public String getMD5(Sync sync) {
        try (InputStream is = Files.newInputStream(Paths.get(locationService.getAbsolutePathForPc(sync)))) {
            return DigestUtils.md5DigestAsHex(is);
        }
    }

    public long getSyncSize(Sync sync) {
        File file = new File(locationService.getAbsolutePathForPc(sync));
        return FileUtils.sizeOf(file);
    }

    @SneakyThrows
    public byte[] getFileContent(Sync sync) {
        File file = new File(locationService.getAbsolutePathForPc(sync));
        return FileUtils.readFileToByteArray(file);
    }

    @SneakyThrows
    public boolean isContentEquals(List<Sync> list) {
        List<File> files = list.stream()
                .map(sync -> new File(locationService.getAbsolutePathForPc(sync)))
                .collect(Collectors.toList());
        File firstFile = files.get(0);
        for (int i = 1; i < files.size(); i++) {
            if (!FileUtils.contentEquals(firstFile, files.get(i))) {
                return false;
            }
        }
        return true;
    }
}
