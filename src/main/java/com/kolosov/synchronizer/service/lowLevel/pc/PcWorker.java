package com.kolosov.synchronizer.service.lowLevel.pc;

import com.kolosov.synchronizer.domain.FileSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.service.LocationService;
import com.kolosov.synchronizer.service.lowLevel.LowLevelWorker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
@Slf4j
public class PcWorker implements LowLevelWorker {

    private final LocationService locationService;

    @Override
    public TreeSync getNewTreeSync() {
        TreeSync newTreeSync = new TreeSync(PC);
        List<Pair<String, String>> folderPairs = locationService.getFolderNamesAndAbsolutePathsForPcRootFolders();
        for (Pair<String, String> pair : folderPairs) {
            File file = new File(pair.getValue());
            FolderSync folderSync = new FolderSync(pair.getKey(), PC, newTreeSync);
            processDirectoryRecursively(file, folderSync);
        }
        return newTreeSync;
    }

    private void processDirectoryRecursively(File parentDirectory, FolderSync parentFolderSync) {
        for (final File file : parentDirectory.listFiles()) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                FolderSync currentFolderSync = new FolderSync(fileName, PC, parentFolderSync);
                processDirectoryRecursively(file, currentFolderSync);
            } else {
                new FileSync(fileName, PC, parentFolderSync);
            }
        }
    }

    @Override
    public void delete(Sync sync) throws IOException {
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
    public void createFolder(FolderSync folderSync) throws IOException {
        boolean created = new File(locationService.getAbsolutePathForPc(folderSync)).mkdir();
        if (!created) {
            throw new IOException();
        }
    }

    @SneakyThrows
    public String getMD5(Sync sync) {
        try (InputStream is = Files.newInputStream(Paths.get(locationService.getAbsolutePathForPc(sync)))) {
            return DigestUtils.md5DigestAsHex(is);
        }
    }

    public long getFileSyncSize(Sync sync) {
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
